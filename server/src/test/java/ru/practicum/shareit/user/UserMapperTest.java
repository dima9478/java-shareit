package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class UserMapperTest {
    @Test
    void toUserDto() {
        User user = User.builder()
                .id(1L)
                .email("e@mail.com")
                .name("name")
                .build();

        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto.getId(), equalTo(user.getId()));
        assertThat(dto.getName(), equalTo(user.getName()));
        assertThat(dto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void toUser() {
        UserDto dto = UserDto.builder()
                .email("e@mail.com")
                .name("name")
                .id(1L)
                .build();

        User user = UserMapper.toUser(dto);

        assertThat(user.getId(), nullValue());
        assertThat(user.getName(), equalTo(dto.getName()));
        assertThat(user.getEmail(), equalTo(dto.getEmail()));
    }
}
