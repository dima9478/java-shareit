package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(CreateRequestDto dto, User user) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest req, List<Item> items) {
        return ItemRequestDto.builder()
                .id(req.getId())
                .description(req.getDescription())
                .created(req.getCreated())
                .items(itemsToGetItemRequestDto(items))
                .build();
    }

    private static List<GetItemRequestItemDto> itemsToGetItemRequestDto(List<Item> items) {
        return items.stream()
                .map(ItemRequestMapper::itemToGetItemsRequestDto)
                .collect(Collectors.toList());
    }

    private static GetItemRequestItemDto itemToGetItemsRequestDto(Item item) {
        return GetItemRequestItemDto.builder()
                .name(item.getName())
                .available(item.isAvailable())
                .requestId(item.getRequest().getId())
                .description(item.getDescription())
                .id(item.getId())
                .build();
    }
}
