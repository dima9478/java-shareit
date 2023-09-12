package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.param.PaginationRequest;

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
    public GetItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long id) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public List<GetItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return itemService.getItems(userId, new PaginationRequest(from, size));
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long id,
                             @RequestBody ItemDto itemDto) {
        return itemService.updateItem(id, userId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItems(@RequestParam String text,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        return itemService.searchItems(text, new PaginationRequest(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId,
                                       @RequestBody CreateCommentDto dto) {
        return itemService.addComment(itemId, userId, dto);
    }
}
