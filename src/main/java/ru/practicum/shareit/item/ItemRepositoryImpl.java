package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();
    Map<Long, List<Item>> userItemIndex = new HashMap<>();
    long currentId;

    @Override
    public Item addItem(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);
        addUserItem(item);

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        removeUserItem(items.get(item.getId()));
        items.put(item.getId(), item);
        addUserItem(item);

        return item;
    }

    @Override
    public Item getItemById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getItemsOfUser(long userId) {
        return userItemIndex.get(userId);
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

    private void addUserItem(Item item) {
        Long userId = item.getOwner().getId();
        List<Item> userItems = userItemIndex.getOrDefault(userId, new ArrayList<>());
        userItems.add(item);
        userItemIndex.putIfAbsent(userId, userItems);
    }

    private void removeUserItem(Item oldItem) {
        Long userId = oldItem.getOwner().getId();
        List<Item> userItems = userItemIndex.get(userId);
        userItems.remove(oldItem);
    }
}
