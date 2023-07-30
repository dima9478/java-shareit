package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto addItem(long userId, @Valid ItemDto itemDto) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User for item doesn't exist: " + userId);
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        Item item = getItem(itemId);
        validate(item, userId);

        Item updatedItem = itemRepository.updateItem(applyPatch(itemDto, item));

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(getItem(itemId));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.getItemsOfUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
        Item item = itemRepository.getItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Cannot find item with id " + itemId);
        }

        return item;
    }
}
