package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueViolationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto user) {

        User newUser;
        try {
            newUser = userRepository.save(UserMapper.toUser(user));
        } catch (DataIntegrityViolationException e) {
            throw new UniqueViolationException("User already exists");
        }

        return UserMapper.toUserDto(newUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User storedUser = getUser(id);

        return UserMapper.toUserDto(userRepository.save(applyPatch(userDto, storedUser)));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto findUserById(long id) {
        User user = getUser(id);

        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
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
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d doesn't exist", userId)));
    }
}
