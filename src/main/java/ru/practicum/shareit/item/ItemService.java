package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, @Valid ItemDto item);

    ItemDto updateItem(long itemId, long userId, ItemDto item);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItems(long userId);

    List<ItemDto> searchItems(String text);
}
