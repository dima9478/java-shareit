package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueViolationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class UserServiceTest {
    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserServiceImpl service;
    @Captor
    private ArgumentCaptor<User> captor;
    private UserDto dto;

    @BeforeEach
    void setUp() {
        dto = UserDto.builder()
                .email("a@mail.com")
                .name("Igor")
                .build();
    }

    @Test
    void createUser_whenSuccess_thenReturnUserDto() {
        when(repository.save(any(User.class))).thenReturn(User.builder()
                .id(1L)
                .email("a@mail.com")
                .name("Igor").build());

        UserDto user = service.createUser(dto);

        verify(repository, times(1)).save(any(User.class));
        assertThat(user.getEmail(), equalTo("a@mail.com"));
        assertThat(user.getId(), equalTo(1L));
    }

    @Test
    void createUser_whenThrowDataIntegrityException_thenThrows() {
        when(repository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UniqueViolationException.class, () -> service.createUser(dto));
    }

    @Test
    void updateUser_whenChangeOneField() {
        dto.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(User.builder()
                .email("op@mail.ru")
                .name("Petr").id(1L)
                .build()));
        when(repository.save(any(User.class))).thenReturn(new User(1L, "Igor", "a@mail.com"));

        service.updateUser(1L, dto);

        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getName(), equalTo(dto.getName()));
    }

    @Test
    void updateUser_whenNoUserInDb_thenThrowNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateUser(1L, dto));
    }

    @Test
    void findUserById_whenNoUser_thenThrowNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findUserById(1L));
    }

    @Test
    void findUserById_whenFound_thenReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(new User(1L, "Igor", "a@mail.ru")));

        UserDto user = service.findUserById(1L);

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo("Igor"));
    }

    @Test
    void getAllUsers_whenFound_thenReturnList() {
        when(repository.findAll()).thenReturn(List.of(
                new User(1L, "I", "m@mail.ru"),
                new User(2L, "O", "y@yandex.com"))
        );

        List<UserDto> users = service.getAllUsers();

        assertThat(users.size(), equalTo(2));
        assertThat(users.get(0).getName(), equalTo("I"));
    }

    @Test
    void deleteUserById_whenDelete() {
        service.deleteUserById(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
