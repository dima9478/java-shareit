package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.param.PaginationRequestConverter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto addItem(long userId, @Valid ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User for item doesn't exist: " + userId));
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("No request with id " + itemDto.getRequestId()));
        }
        Item item = ItemMapper.toItem(itemDto, user, request);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        Item item = getItem(itemId);
        validate(item, userId);

        Item updatedItem = itemRepository.save(applyPatch(itemDto, item));

        return ItemMapper.toItemDto(updatedItem);
    }

    @Transactional(readOnly = true)
    @Override
    public GetItemDto getItemById(long itemId, long userId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<Booking> lastBooking = Collections.emptyList();
        List<Booking> nextBooking = Collections.emptyList();
        Item item = getItem(itemId);
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime time = LocalDateTime.now();
            lastBooking = bookingRepository.findLastBookingsOfItem(itemId, time, Pageable.ofSize(1));
            nextBooking = bookingRepository.findNextBookingsOfItem(itemId, time, Pageable.ofSize(1));
        }
        return ItemMapper.toItemDto(
                item,
                lastBooking.isEmpty() ? null : lastBooking.get(0),
                nextBooking.isEmpty() ? null : nextBooking.get(0),
                comments
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetItemDto> getItems(long userId, @Valid PaginationRequest pagRequest) {
        List<Item> items = itemRepository.findAllByOwnerId(
                userId,
                PaginationRequestConverter.toPageable(pagRequest, Sort.by(Sort.Direction.ASC, "id")));
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        LocalDateTime time = LocalDateTime.now();
        Map<Long, Booking> lastBookings = bookingRepository.findLastBookingOfItems(itemIds, time).stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), Function.identity()));
        Map<Long, Booking> nextBookings = bookingRepository.findNextBookingOfItems(itemIds, time).stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), Function.identity()));
        Map<Long, List<Comment>> comments = commentRepository.findByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
        return items.stream()
                .map(i -> ItemMapper.toItemDto(
                        i,
                        lastBookings.get(i.getId()),
                        nextBookings.get(i.getId()),
                        comments.get(i.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItems(String text, @Valid PaginationRequest pagRequest) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchItems(
                        text, PaginationRequestConverter.toPageable(pagRequest, Sort.by(Sort.Direction.ASC, "id"))
                ).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(long itemId, long userId, @Valid CreateCommentDto dto) {
        Item item = getItem(itemId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));


        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                || commentRepository.existsByAuthorIdAndItemId(userId, itemId)) {
            throw new BadRequestException("User cannot leave comment");
        }

        Comment comment = Comment.builder()
                .author(user)
                .text(dto.getText())
                .item(item)
                .created(LocalDateTime.now())
                .build();
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Item applyPatch(ItemDto itemDto, Item item) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();

        return Item.builder()
                .id(item.getId())
                .name(name != null ? name : item.getName())
                .description(description != null ? description : item.getDescription())
                .available(isAvailable != null ? isAvailable : item.isAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    private void validate(Item item, long userId) {
        if (item.getOwner().getId() != userId) {
            throw new AccessDeniedException("User don't have access to the item");
        }
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cannot find item with id " + itemId));
    }
}
