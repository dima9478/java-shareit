package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UniqueViolationException;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId;

    @Override
    public User addUser(User user) {
        validate(user);

        user.setId(++currentId);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        validate(user, u -> !u.getId().equals(user.getId()));

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }


    @Override
    public void deleteUserById(long id) {
        users.remove(id);
    }

    private void validate(User user) {
        if (users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UniqueViolationException(String.format("User with email %s already exists", user.getEmail()));
        }
    }

    private void validate(User user, Predicate<User> filter) {
        if (users.values().stream()
                .filter(filter)
                .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UniqueViolationException(String.format("User with email %s already exists", user.getEmail()));
        }
    }
}
