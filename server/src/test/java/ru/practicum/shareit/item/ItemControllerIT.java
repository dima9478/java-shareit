package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.param.PaginationRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerIT {
    @MockBean
    private ItemService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final ItemDto dto = ItemDto.builder()
            .requestId(1L)
            .name("name")
            .id(1L)
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
    private final String userHeader = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void addItem_whenNoRequestOrUser_thenReturn404() {
        when(service.addItem(1L, dto)).thenThrow(NotFoundException.class);

        mvc.perform(post("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addItem_whenNoUserInHeader_thenReturn500() {
        when(service.addItem(1L, dto)).thenReturn(dto);

        mvc.perform(post("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void addItem_whenSuccessful_thenReturn200() {
        when(service.addItem(1L, dto)).thenReturn(dto);

        mvc.perform(post("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.requestId", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void getItem_whenNoItemOrUser_thenReturn404() {
        when(service.getItemById(1L, 1L)).thenThrow(NotFoundException.class);

        mvc.perform(get("/items/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getItem_whenSuccessful_thenReturn200() {
        when(service.getItemById(1L, 1L)).thenReturn(getDto);

        mvc.perform(get("/items/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.comments", empty()))
                .andExpect(jsonPath("$.lastBooking", notNullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()));
    }

    @SneakyThrows
    @Test
    void getUserItems_whenSuccessful_thenReturn200() {
        when(service.getItems(1L, new PaginationRequest(0, 1)))
                .thenReturn(List.of(getDto));

        mvc.perform(get("/items?from=0&size=1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].comments", empty()))
                .andExpect(jsonPath("$[0].lastBooking", notNullValue()))
                .andExpect(jsonPath("$[0].nextBooking", nullValue()));
    }

    @SneakyThrows
    @Test
    void patchItem_whenNoItem_thenReturn404() {
        when(service.updateItem(1L, 1L, dto)).thenThrow(NotFoundException.class);

        mvc.perform(patch("/items/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void patchItem_whenAccessDenied_thenReturn403() {
        when(service.updateItem(1L, 2L, dto)).thenThrow(AccessDeniedException.class);

        mvc.perform(patch("/items/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 2L)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    void patchItem_whenSuccessful_thenReturn200() {
        when(service.updateItem(1L, 1L, dto)).thenReturn(dto);

        mvc.perform(patch("/items/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("desc")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @SneakyThrows
    @Test
    void searchForItems_whenBadRequest_thenReturn400() {
        when(service.searchItems("", new PaginationRequest(0, 1)))
                .thenThrow(BadRequestException.class);

        mvc.perform(get("/items/search?text=&from=0&size=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void searchForItems_whenSuccessful_thenReturn200() {
        when(service.searchItems("text", new PaginationRequest(0, 1)))
                .thenReturn(List.of(dto));

        mvc.perform(get("/items/search?text=text&from=0&size=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].description", is("desc")))
                .andExpect(jsonPath("$[0].name", is("name")));
    }

    @SneakyThrows
    @Test
    void addCommentToItem_whenBadRequest_thenReturn400() {
        CreateCommentDto commentDto = new CreateCommentDto("text");
        when(service.addComment(1L, 1L, commentDto))
                .thenThrow(BadRequestException.class);

        mvc.perform(post("/items/{id}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addCommentToItem_whenNotFound_thenReturn404() {
        CreateCommentDto commentDto = new CreateCommentDto("text");
        when(service.addComment(1L, 1L, commentDto))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items/{id}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addCommentToItem_whenSuccessful_thenReturn200() {
        CreateCommentDto commentDto = new CreateCommentDto("text");
        when(service.addComment(1L, 1L, commentDto))
                .thenReturn(CommentDto.builder()
                        .id(1L)
                        .authorName("name")
                        .created(LocalDateTime.now())
                        .text("text")
                        .build());

        mvc.perform(post("/items/{id}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.authorName", is("name")))
                .andExpect(jsonPath("$.text", is("text")));
    }
}
