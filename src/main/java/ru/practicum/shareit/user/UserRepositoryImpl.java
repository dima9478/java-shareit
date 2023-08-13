package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UniqueViolationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private long currentId;

    @Override
    public User addUser(User user) {
        validate(user);

        user.setId(++currentId);
        users.put(user.getId(), user);
        emailUniqSet.add(user.getEmail());

        return user;
    }

    @Override
    public User updateUser(User user) {
        User oldUser = users.get(user.getId());
        validateOnUpdate(user, oldUser);

        users.put(user.getId(), user);
        emailUniqSet.add(user.getEmail());
        emailUniqSet.remove(oldUser.getEmail());

        return user;
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }


    @Override
    public void deleteUserById(long id) {
        String email = users.get(id).getEmail();
        users.remove(id);
        emailUniqSet.remove(email);
    }

    private void validate(User user) {
        if (emailUniqSet.contains(user.getEmail())) {
            throw new UniqueViolationException(String.format("User with email %s already exists", user.getEmail()));
        }
    }

    private void validateOnUpdate(User user, User oldUser) {
        if (!user.getEmail().equals(oldUser.getEmail()) && emailUniqSet.contains(user.getEmail())) {
            throw new UniqueViolationException(String.format("User with email %s already exists", user.getEmail()));
        }
    }
}
