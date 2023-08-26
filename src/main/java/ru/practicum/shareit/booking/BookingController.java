package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody BookingCreateDto dto) {
        return bookingService.addBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    BookingDto changeBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam boolean approved) {
        return bookingService.finalizeBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    List<BookingDto> getBookingByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsByState(userId, state);
    }

    @GetMapping("/owner")
    List<BookingDto> getOwnerBookingByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookingsByState(userId, state);
    }
}
