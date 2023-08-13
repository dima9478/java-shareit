package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @RequestBody ItemDto item) {
        return itemService.addItem(userId, item);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable long id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long id,
                             @RequestBody ItemDto itemDto) {
        return itemService.updateItem(id, userId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
