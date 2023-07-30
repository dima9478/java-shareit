package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    User getUserById(long id);

    List<User> getUsers();

    void deleteUserById(long id);
}
