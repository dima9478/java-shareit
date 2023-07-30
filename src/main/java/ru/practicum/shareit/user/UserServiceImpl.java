package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(@Valid UserDto user) {
        User newUser = userRepository.addUser(UserMapper.toUser(user));

        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User storedUser = getUser(id);

        return UserMapper.toUserDto(userRepository.updateUser(applyPatch(userDto, storedUser)));
    }

    @Override
    public UserDto findUserById(long id) {
        User user = getUser(id);

        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }

    private User applyPatch(UserDto userPatch, User storedUser) {
        String patchEmail = userPatch.getEmail();
        String patchName = userPatch.getName();

        return User.builder()
                .id(storedUser.getId())
                .email(patchEmail != null ? patchEmail : storedUser.getEmail())
                .name(patchName != null ? patchName : storedUser.getName())
                .build();
    }

    private User getUser(long userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id %d doesn't exist", userId));
        }

        return user;
    }
}
