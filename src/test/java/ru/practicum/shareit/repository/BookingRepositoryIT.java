package ru.practicum.shareit.repository;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest(properties = "db.name=test3")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryIT {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User(1L, "Igor", "e@mail.ru");
        User user2 = new User(2L, "Petr", "p@goole.com");
        User user3 = new User(3L, "Valya", "v@wer.com");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        userRepository.save(user3);

        Item item1 = Item.builder()
                .id(1L)
                .owner(user1)
                .name("mane1")
                .description("desc1")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .owner(user2)
                .name("mn2")
                .description("name2")
                .available(true)
                .build();
        Item item3 = Item.builder()
                .id(3L)
                .owner(user1)
                .name("name3")
                .description("desc3")
                .available(false)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(12))
                .end(LocalDateTime.now().plusHours(24))
                .status(BookingStatus.WAITING)
                .item(item1)
                .booker(user2)
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusHours(24))
                .end(LocalDateTime.now().minusHours(12))
                .status(BookingStatus.APPROVED)
                .item(item2)
                .booker(user1)
                .build();
        Booking booking3 = Booking.builder()
                .id(3L)
                .start(LocalDateTime.now().minusHours(3))
                .end(LocalDateTime.now().plusHours(3))
                .status(BookingStatus.APPROVED)
                .item(item3)
                .booker(user2)
                .build();
        Booking booking4 = Booking.builder()
                .id(4L)
                .start(LocalDateTime.now().minusHours(12))
                .end(LocalDateTime.now().minusHours(6))
                .status(BookingStatus.WAITING)
                .item(item3)
                .booker(user1)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);
    }

    @Test
    void findByBookerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(1L, BookingStatus.APPROVED, PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(2L));
        ///
        bookings = bookingRepository.findByBookerIdAndStatus(1L, BookingStatus.WAITING, PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void findByBookerIdAndStatusIn() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusIn(
                2L,
                Set.of(BookingStatus.WAITING, BookingStatus.APPROVED),
                PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getStatus(), anyOf(equalTo(BookingStatus.APPROVED), equalTo(BookingStatus.WAITING)));
        assertThat(bookings.get(1).getStatus(), anyOf(equalTo(BookingStatus.APPROVED), equalTo(BookingStatus.WAITING)));
    }

    @Test
    void findByBookerId() {
        List<Booking> bookings = bookingRepository.findByBookerId(2L, PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(2L));
        assertThat(bookings.get(1).getBooker().getId(), equalTo(2L));
    }

    @Test
    void existsByBookerIdAndItemIdAndEndBefore() {
        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(1L, 2L, LocalDateTime.now());

        assertThat(exists, equalTo(true));
    }

    @Test
    void findByBookerIdAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(1L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), equalTo(2L));
        assertThat(bookings.get(1).getId(), equalTo(4L));
    }

    @Test
    void findByBookerIdCurrent() {
        List<Booking> bookings = bookingRepository.findByBookerIdCurrent(2L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(3L));

        //
        bookings = bookingRepository.findByBookerIdCurrent(1L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings, empty());
    }

    @Test
    void findByBookerIdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfter(2L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void findByItemOwnerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatus(1L, BookingStatus.WAITING, PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), equalTo(1L));
        assertThat(bookings.get(1).getId(), equalTo(4L));
    }

    @Test
    void findByItemOwnerIdAndStatusIn() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatusIn(
                1L,
                Set.of(BookingStatus.APPROVED, BookingStatus.REJECTED),
                PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(3L));
    }

    @Test
    void findByItemOwnerId() {
        List<Booking> bookings = bookingRepository.findByItemOwnerId(1L, PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(3));
    }

    @Test
    void findByItemOwnerIdAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(1L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(4L));
    }

    @Test
    void findByItemOwnerIdCurrent() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdCurrent(1L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(3L));
    }

    @Test
    void findByItemOwnerIdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(1L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void findLastBookingsOfItem() {
        List<Booking> bookings = bookingRepository.findLastBookingsOfItem(3L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), equalTo(3L));
        assertThat(bookings.get(1).getId(), equalTo(4L));
    }

    @Test
    void findNextBookingsOfItem() {
        List<Booking> bookings = bookingRepository.findNextBookingsOfItem(1L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));

        ///

        bookings = bookingRepository.findNextBookingsOfItem(2L, LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings, empty());
    }

    @Test
    void findLastBookingOfItems() {
        List<Booking> bookings = bookingRepository.findLastBookingOfItems(List.of(1L, 2L, 3L), LocalDateTime.now());

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.stream()
                .map(Booking::getId)
                .collect(Collectors.toList()), containsInAnyOrder(equalTo(2L), equalTo(3L)));
    }

    @Test
    void findNextBookingOfItems() {
        List<Booking> bookings = bookingRepository.findNextBookingOfItems(List.of(1L, 2L, 3L), LocalDateTime.now());

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }
}
