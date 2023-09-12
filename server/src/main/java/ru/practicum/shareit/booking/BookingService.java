package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.param.PaginationRequest;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingCreateDto bookingDto, long userId);

    BookingDto finalizeBookingStatus(long bookingId, long userId, boolean approved);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getBookingsByState(long userId, String state, PaginationRequest pagRequest);

    List<BookingDto> getOwnerBookingsByState(long userId, String state, PaginationRequest pagRequest);
}
