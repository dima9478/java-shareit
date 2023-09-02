package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.param.PaginationRequestConverter;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto addRequest(long userId, @Valid CreateRequestDto dto) {
        ItemRequest req = ItemRequestMapper.toItemRequest(dto, getUser(userId));

        return ItemRequestMapper.toDto(requestRepository.save(req), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        getUser(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(
                userId,
                Sort.by(Sort.Direction.DESC, "created")
        );

        return makeItemRequestDtoList(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllRequests(long userId, @Valid PaginationRequest pagRequest) {
        getUser(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(
                userId,
                PaginationRequestConverter.toPageable(pagRequest, Sort.by(Sort.Direction.DESC, "created"))
        );

        return makeItemRequestDtoList(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getRequest(long userId, long requestId) {
        getUser(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request no found with id " + requestId));
        List<Item> items = itemRepository.findAllByRequestId(requestId);

        return ItemRequestMapper.toDto(request, items);
    }

    private List<ItemRequestDto> makeItemRequestDtoList(List<ItemRequest> requests) {
        Set<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);
        Map<Long, List<Item>> reqIdToItems = items.stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));

        return requests.stream()
                .map(r -> ItemRequestMapper.toDto(r, reqIdToItems.getOrDefault(r.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User for item doesn't exist: " + userId));
    }
}
