package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService service;
    @InjectMocks
    private UserController controller;
    private final UserDto dto = UserDto.builder()
            .name("Igor")
            .email("a@mail.ru")
            .build();

    @Test
    void addUser() {
        UserDto dto1 = dto.toBuilder().id(1L).build();
        when(service.createUser(any(UserDto.class))).thenReturn(dto1);

        assertThat(controller.createUser(dto), equalTo(dto1));
    }

    @Test
    void updateUser() {
        UserDto dto1 = dto.toBuilder().id(1L).build();
        when(service.updateUser(1L, dto)).thenReturn(dto1);

        assertThat(controller.updateUser(1L, dto), equalTo(dto1));
    }

    @Test
    void getUser() {
        UserDto dto1 = dto.toBuilder().id(1L).build();
        when(service.findUserById(1L)).thenReturn(dto1);

        assertThat(controller.getUser(1L), equalTo(dto1));
    }

    @Test
    void getUsers() {
        UserDto dto1 = dto.toBuilder().id(1L).build();
        when(service.getAllUsers()).thenReturn(List.of(dto1));

        assertThat(controller.getUsers().get(0), equalTo(dto1));
    }

    @Test
    void deleteUser() {
        controller.deleteUser(1L);

        verify(service, times(1)).deleteUserById(1L);
    }
}
