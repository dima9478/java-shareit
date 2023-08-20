package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public static Booking createDtoToBooking(BookingCreateDto dto, User booker, Item item) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .end(booking.getEnd())
                .start(booking.getStart())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .build();
    }

    public static ItemBookingDto toItemBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return ItemBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .build();
    }
}
