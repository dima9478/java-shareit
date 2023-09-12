package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CommentMapperTest {
    @Test
    void toCommentDto() {
        LocalDateTime created = LocalDateTime.of(2015, 11, 23, 5, 34);
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .author(new User(1L, "nname", "e@mail.com"))
                .created(created)
                .build();

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertThat(dto.getId(), equalTo(comment.getId()));
        assertThat(dto.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(dto.getText(), equalTo(comment.getText()));
        assertThat(dto.getCreated(), equalTo(comment.getCreated()));
    }
}
