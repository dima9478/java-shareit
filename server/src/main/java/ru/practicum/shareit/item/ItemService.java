package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.param.PaginationRequest;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto item);

    ItemDto updateItem(long itemId, long userId, ItemDto item);

    GetItemDto getItemById(long itemId, long userId);

    List<GetItemDto> getItems(long userId, PaginationRequest pagRequest);

    List<ItemDto> searchItems(String text, PaginationRequest pagRequest);

    CommentDto addComment(long itemId, long userId, CreateCommentDto dto);
}
