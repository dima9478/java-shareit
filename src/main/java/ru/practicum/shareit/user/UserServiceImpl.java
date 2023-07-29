package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueViolationException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(@Valid User user) {
        User newUser = userRepository.addUser(user);

        return newUser;
    }

    @Override
    public User updateUser(long id, User user) {
        User storedUser = findUserById(id);

        user.setId(id);
        return userRepository.updateUser(applyPatch(user, storedUser));
    }

    @Override
    public User findUserById(long id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new NotFoundException(String.format("User with id %d doesn't exist", id));
        }

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getUsers();
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }

    private User applyPatch(User userPatch, User storedUser) {
        String patchEmail = userPatch.getEmail();
        String patchName = userPatch.getName();

        return User.builder()
                .id(storedUser.getId())
                .email(patchEmail != null ? patchEmail : storedUser.getEmail())
                .name(patchName != null ? patchName : storedUser.getName())
                .build();
    }
}
