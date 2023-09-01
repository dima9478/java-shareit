package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.param.PaginationRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService service;
    @InjectMocks
    private ItemController controller;
    private final ItemDto dto = ItemDto.builder()
            .requestId(1L)
            .name("name")
            .description("desc")
            .available(true)
            .build();
    private final GetItemDto getDto = GetItemDto.builder()
            .id(1L)
            .comments(List.of())
            .description("desc")
            .name("name")
            .lastBooking(ItemBookingDto.builder().id(1L)
                    .start(LocalDateTime.of(2012, 12, 23, 12, 34))
                    .end(LocalDateTime.of(2015, 12, 23, 12, 34))
                    .bookerId(2L)
                    .build())
            .build();

    @Test
    void addItem() {
        ItemDto dto1 = dto.toBuilder().id(1L).build();
        when(service.addItem(1L, dto)).thenReturn(dto1);

        assertThat(controller.addItem(1L, dto), equalTo(dto1));
    }

    @Test
    void getItem() {
        when(service.getItemById(1L, 1L)).thenReturn(getDto);

        assertThat(controller.getItem(1L, 1L), equalTo(getDto));
    }

    @Test
    void getUserItems() {
        PaginationRequest req = new PaginationRequest(0, 10);
        when(service.getItems(1L, req)).thenReturn(List.of(getDto));

        assertThat(controller.getUserItems(1L, 0, 10), equalTo(List.of(getDto)));
    }

    @Test
    void patchItem() {
        ItemDto dto1 = dto.toBuilder().id(1L).name("alt_name").build();
        when(service.updateItem(1L, 1L, dto)).thenReturn(dto1);

        assertThat(controller.patchItem(1L, 1L, dto), equalTo(dto1));
    }

    @Test
    void searchForItems() {
        when(service.searchItems("text", new PaginationRequest(0, 10))).thenReturn(List.of(dto));

        assertThat(controller.searchForItems("text", 0, 10), equalTo(List.of(dto)));
    }

    @Test
    void addCommentToItem() {
        CommentDto commentDto = CommentDto.builder()
                .authorName("auuthor")
                .text("text")
                .id(1L)
                .build();
        when(service.addComment(1L, 1L, new CreateCommentDto("text"))).thenReturn(commentDto);

        assertThat(controller.addCommentToItem(1L, 1L, new CreateCommentDto("text")), equalTo(commentDto));
    }
}
