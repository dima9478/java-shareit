package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();
    long currentId;

    @Override
    public Item addItem(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item getItemById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getItemsOfUser(long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        String textLower = text.toLowerCase();
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(i -> i.getName().toLowerCase().contains(textLower) ||
                        i.getDescription().toLowerCase().contains(textLower))
                .collect(Collectors.toList());
    }
}
