package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.param.PaginationRequest;

import javax.validation.Valid;
import java.util.List;

public interface BookingService {
    BookingDto addBooking(@Valid BookingCreateDto bookingDto, long userId);

    BookingDto finalizeBookingStatus(long bookingId, long userId, boolean approved);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getBookingsByState(long userId, String state, @Valid PaginationRequest pagRequest);

    List<BookingDto> getOwnerBookingsByState(long userId, String state, @Valid PaginationRequest pagRequest);
}
