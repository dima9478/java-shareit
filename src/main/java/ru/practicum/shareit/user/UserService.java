package ru.practicum.shareit.user;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    User createUser(@Valid User user);

    User updateUser(long id, User user);

    User findUserById(long id);

    List<User> getAllUsers();

    void deleteUserById(long id);
}
