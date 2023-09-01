package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerIT {
    @MockBean
    private ItemRequestService service;
    private ObjectMapper mapper;
    private MockMvc mvc;
    private final String userHeader = "X-Sharer-User-Id";
    private final GetItemRequestItemDto itemDto = GetItemRequestItemDto.builder()
            .requestId(1L)
            .id(1L)
            .available(true)
            .description("i_desc")
            .build();
    private final ItemRequestDto dto = ItemRequestDto.builder()
            .id(1L)
            .description("desc1")
            .created(LocalDateTime.now())
            .items(List.of(itemDto))
            .build();

    @SneakyThrows
    @Test
    void addRequest_whenNotFound_thenReturn404() {
        CreateRequestDto createDto = new CreateRequestDto("desc");
        when(service.addRequest(1L, createDto)).thenThrow(NotFoundException.class);

        mvc.perform(post("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addRequest_whenSuccessful_thenReturnDto() {
        CreateRequestDto createDto = new CreateRequestDto("desc");
        when(service.addRequest(1L, createDto)).thenReturn(dto);

        mvc.perform(post("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1L), Long.class))
                .andExpect(jsonPath("$.description", equalTo("desc1")))
                .andExpect(jsonPath("$.items", notNullValue()));
    }

    @SneakyThrows
    @Test
    void getUserRequests_whenNotFound_thenReturn404() {
        when(service.getUserRequests(1L)).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getUserRequests_whenSuccessful_thenReturn200() {
        when(service.getUserRequests(1L)).thenReturn(List.of(dto));

        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(1)))
                .andExpect(jsonPath("$[0].id", equalTo(1L), Long.class))
                .andExpect(jsonPath("$[0].items", iterableWithSize(1)));
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenNotFound_thenReturn404() {
        when(service.getAllRequests(1L, new PaginationRequest(0, 10))).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenSuccessful_thenReturn200() {
        when(service.getAllRequests(1L, new PaginationRequest(0, 10))).thenReturn(List.of(dto));

        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(1)))
                .andExpect(jsonPath("$[0].id", equalTo(1L), Long.class))
                .andExpect(jsonPath("$[0].items", iterableWithSize(1)));
    }

    @SneakyThrows
    @Test
    void getRequest_whenNotFound_thenReturn404() {
        when(service.getRequest(1L, 1L)).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getRequest_whenSuccessful_thenReturn200() {
        when(service.getRequest(1L, 1L)).thenReturn(dto);

        mvc.perform(get("/requests/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1L), Long.class))
                .andExpect(jsonPath("$.description", equalTo("desc1")))
                .andExpect(jsonPath("$.items", iterableWithSize(1)));
    }
}
