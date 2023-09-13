package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueViolationException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerIT {
    @MockBean
    private UserService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final UserDto dto = UserDto.builder()
            .name("Igor")
            .email("a@mail.ru")
            .build();

    @SneakyThrows
    @Test
    void createUser_whenUniqueViolation_thenReturn409() {
        when(service.createUser(any(UserDto.class))).thenThrow(UniqueViolationException.class);

        mvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @SneakyThrows
    @Test
    void createUser_whenCorrectBody_thenReturn200() {
        when(service.createUser(any(UserDto.class))).thenReturn(dto.toBuilder().id(1L).build());

        mvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Igor")));
    }

    @SneakyThrows
    @Test
    void updateUser_whenNoUser_thenReturn404() {
        when(service.updateUser(anyLong(), any(UserDto.class))).thenThrow(NotFoundException.class);

        mvc.perform(patch("/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateUser_whenSuccessfulUpdate_thenReturn200() {
        when(service.updateUser(anyLong(), any(UserDto.class))).thenReturn(dto.toBuilder().id(1L).build());

        mvc.perform(patch("/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Igor")));
    }

    @SneakyThrows
    @Test
    void getUser_whenNoUser_thenReturn404() {
        when(service.findUserById(1L)).thenThrow(NotFoundException.class);

        mvc.perform(get("/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getUser_whenUser_thenReturn200() {
        when(service.findUserById(1L)).thenReturn(dto.toBuilder().id(1L).build());

        mvc.perform(get("/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Igor")));
    }

    @SneakyThrows
    @Test
    void getUsers_thenReturn200() {
        when(service.getAllUsers()).thenReturn(List.of(
                dto.toBuilder().id(1L).build(),
                dto.toBuilder().id(2L).build())
        );

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("Igor")))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class));
    }

    @SneakyThrows
    @Test
    void deleteUser_whenNoUser_thenReturn204() {
        mvc.perform(delete("/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
