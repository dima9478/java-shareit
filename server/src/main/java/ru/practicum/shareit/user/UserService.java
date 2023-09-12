package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto updateUser(long id, UserDto user);

    UserDto findUserById(long id);

    List<UserDto> getAllUsers();

    void deleteUserById(long id);
}
