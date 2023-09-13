package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingMapperTest {
    private final Booking booking = Booking.builder()
            .id(1L)
            .item(Item.builder()
                    .id(2L)
                    .name("nameI")
                    .description("desc")
                    .build())
            .status(BookingStatus.WAITING)
            .start(LocalDateTime.of(2012, 12, 23, 12, 34))
            .end(LocalDateTime.of(2013, 12, 23, 12, 34))
            .booker(new User(3L, "nameU", "u@mail.com"))
            .build();

    @Test
    void createDtoToBooking() {

        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        User booker = new User(1L, "nameq", "q@mail.com");
        Item item = Item.builder()
                .id(1L).build();

        Booking booking1 = BookingMapper.createDtoToBooking(dto, booker, item);

        assertThat(booking1.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(booking1.getBooker(), equalTo(booker));
        assertThat(booking1.getStart(), equalTo(dto.getStart()));
        assertThat(booking1.getEnd(), equalTo(dto.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(1L));
    }

    @Test
    void toDto() {
        BookingDto dto = BookingMapper.toDto(booking);

        assertThat(dto.getStatus(), equalTo(booking.getStatus()));
        assertThat(dto.getBooker().getName(), equalTo(booking.getBooker().getName()));
        assertThat(dto.getStart(), equalTo(booking.getStart()));
        assertThat(dto.getEnd(), equalTo(booking.getEnd()));
        assertThat(dto.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(dto.getId(), equalTo(booking.getId()));
    }

    @Test
    void toItemBookingDto() {
        ItemBookingDto dto = BookingMapper.toItemBookingDto(booking);

        assertThat(dto.getId(), equalTo(booking.getId()));
        assertThat(dto.getStart(), equalTo(booking.getStart()));
        assertThat(dto.getEnd(), equalTo(booking.getEnd()));
        assertThat(dto.getBookerId(), equalTo(booking.getBooker().getId()));
    }
}
