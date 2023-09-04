package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ItemMapperTest {
    private final Item item = Item.builder()
            .id(1L)
            .name("name1")
            .available(true)
            .description("desc1")
            .request(new ItemRequest(
                    2L,
                    "descr", new User(1L, "uname", "email.com"),
                    LocalDateTime.now())
            )
            .build();

    @Test
    void toItemDto() {
        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getId(), equalTo(item.getId()));
        assertThat(dto.getName(), equalTo(item.getName()));
        assertThat(dto.getDescription(), equalTo(item.getDescription()));
        assertThat(dto.getAvailable(), equalTo(item.isAvailable()));
        assertThat(dto.getRequestId(), equalTo(item.getRequest().getId()));
    }

    @Test
    void toGetItemDto() {
        LocalDateTime start = LocalDateTime.of(2011, 12, 23, 23, 34);
        LocalDateTime end = LocalDateTime.of(2012, 12, 23, 23, 34);
        Booking lastBooking = Booking.builder()
                .booker(new User(2L, "name2", "mail2"))
                .status(BookingStatus.APPROVED)
                .item(item)
                .start(start)
                .end(end)
                .build();
        List<Comment> comments = List.of(Comment.builder()
                .author(new User(3L, "name3", "email3"))
                .text("text")
                .id(2L)
                .created(LocalDateTime.now()).build());

        GetItemDto dto = ItemMapper.toItemDto(item, lastBooking, null, comments);

        assertThat(dto.getId(), equalTo(item.getId()));
        assertThat(dto.getName(), equalTo(item.getName()));
        assertThat(dto.getDescription(), equalTo(item.getDescription()));
        assertThat(dto.getAvailable(), equalTo(item.isAvailable()));
        assertThat(dto.getLastBooking(), notNullValue());
        assertThat(dto.getNextBooking(), nullValue());
        assertThat(dto.getComments().size(), equalTo(1));
        assertThat(dto.getComments().get(0).getId(), equalTo(2L));
    }

    @Test
    void toItem() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .requestId(2L)
                .available(true)
                .description("dd")
                .name("nn")
                .build();
        User user = new User(2L, "rr", "m@d.com");
        ItemRequest request = item.getRequest();

        Item item2 = ItemMapper.toItem(dto, user, request);

        assertThat(item2.getId(), nullValue());
        assertThat(item2.getName(), equalTo(dto.getName()));
        assertThat(item2.getDescription(), equalTo(dto.getDescription()));
        assertThat(item2.isAvailable(), equalTo(dto.getAvailable()));
        assertThat(item2.getOwner(), equalTo(user));
        assertThat(item2.getRequest(), equalTo(request));
    }
}
