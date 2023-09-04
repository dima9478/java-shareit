package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.ShareItTests;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingIT extends ShareItTests {
    @Autowired
    protected BookingIT(ItemService itemService, BookingService bookingService, ItemRequestService requestService, UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository, ItemRequestRepository requestRepository, CommentRepository commentRepository) {
        super(itemService, bookingService, requestService, userRepository, itemRepository, bookingRepository, requestRepository, commentRepository);
    }

    @Test
    void getBookingsByState() {
        List<BookingDto> bookings = bookingService.getBookingsByState(2L, "CURRENT", new PaginationRequest(0, 5));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(3L));

        ///

        bookings = bookingService.getBookingsByState(2L, "WAITING", new PaginationRequest(0, 5));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(4L));

        ///

        bookings = bookingService.getBookingsByState(1L, "REJECTED", new PaginationRequest(0, 5));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(2L));
    }

    @Test
    void getOwnerBookingsByState() {
        List<BookingDto> bookings = bookingService.getOwnerBookingsByState(1L, "CURRENT", new PaginationRequest(0, 5));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(3L));

        ///

        bookings = bookingService.getOwnerBookingsByState(1L, "WAITING", new PaginationRequest(0, 5));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(4L));

        ///

        bookings = bookingService.getOwnerBookingsByState(2L, "REJECTED", new PaginationRequest(0, 5));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(2L));
    }
}
