package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long id);

    List<Item> getItemsOfUser(long userId);

    List<Item> searchItems(String text);
}
