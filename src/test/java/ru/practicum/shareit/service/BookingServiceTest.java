package ru.practicum.shareit.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl service;
    @Captor
    private ArgumentCaptor<Booking> captor;
    private final User user = new User(1L, "name", "mail.com");
    private final User user2 = new User(2L, "name2", "mw@g.com");
    private final Item item = Item.builder()
            .id(1L)
            .owner(user)
            .description("desc")
            .name("name")
            .available(true)
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(user2)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(12))
            .item(item)
            .build();

    @Test
    void addBooking_whenEndBeforeStart_thenThrowValidationException() {
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(12));

        assertThrows(ValidationException.class, () -> service.addBooking(dto, 1L));
    }

    @Test
    void addBooking_whenNoItem_thenThrowNotFound() {
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusHours(12), LocalDateTime.now());
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> service.addBooking(dto, 1L));
    }

    @Test
    void addBooking_whenUserIsOwner_thenThrowNotFound() {
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusHours(12), LocalDateTime.now());
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> service.addBooking(dto, 1L));
    }

    @Test
    void addBooking_whenSuccess_thenReturnDto() {
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusHours(12), LocalDateTime.now());
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto bookingDto = service.addBooking(dto, 2L);

        assertThat(bookingDto.getId(), Matchers.equalTo(1L));
        assertThat(bookingDto.getBooker().getName(), Matchers.equalTo("name2"));
    }

    @Test
    void finalizeBookingStatus_whenNoBooking_thenReturnNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.finalizeBookingStatus(1L, 1L, true));
    }

    @Test
    void finalizeBookingStatus_whenStateConsidered_thenReturnIllegalArgument() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> service.finalizeBookingStatus(1L, 1L, true));
    }

    @Test
    void finalizeBookingStatus_whenSuccessful_thenSetStatusApproved() {
        Booking booking2 = booking.toBuilder().status(BookingStatus.WAITING).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.finalizeBookingStatus(1L, 1L, true);

        verify(bookingRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getStatus(), Matchers.equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingById_whenNoBooking_thenThrowNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getBookingById(1L, 1L));
    }

    @Test
    void getBookingById_whenUserNotOwnerOrBooker_thenThrowNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> service.getBookingById(1L, 5L));
    }

    @Test
    void getBookingById_whenSuccessful_thenReturnDto() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto dto = service.getBookingById(1L, 1L);

        assertThat(dto.getId(), Matchers.equalTo(1L));
        assertThat(dto.getBooker().getId(), Matchers.equalTo(2L));
    }

    @Test
    void getBookingsByState_whenIllegalState_thenThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getBookingsByState(1L, "Fake", new PaginationRequest(0, 5)));
    }

    @Test
    void getBookingsByState_whenNoBooker_thenThrowNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getBookingsByState(1L, "CURRENT", new PaginationRequest(0, 5)));
    }

    @Test
    void getBookingsByState_whenAll() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = service.getBookingsByState(1L, "ALL", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1)).findByBookerId(anyLong(), any(Pageable.class));
        assertThat(bookings.get(0).getItem().getDescription(), Matchers.equalTo("desc"));
    }

    @Test
    void getBookingsByState_whenByTime() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.getBookingsByState(1L, "CURRENT", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1))
                .findByBookerIdCurrent(anyLong(), any(LocalDateTime.class), any(Pageable.class));

        ///////

        service.getBookingsByState(1L, "PAST", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));

        ///////

        service.getBookingsByState(1L, "FUTURE", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getBookingsByState_whenByStatus() {
        ArgumentCaptor<BookingStatus> statusCaptor = forClass(BookingStatus.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.getBookingsByState(1L, "WAITING", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1)).findByBookerIdAndStatus(anyLong(),
                statusCaptor.capture(),
                any(Pageable.class));
        assertThat(statusCaptor.getValue(), Matchers.equalTo(BookingStatus.WAITING));
    }

    @Test
    void getOwnerBookingsByState_whenIllegalState_thenThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getOwnerBookingsByState(1L, "Fake", new PaginationRequest(0, 5)));
    }

    @Test
    void getOwnerBookingsByState_whenNoBooker_thenThrowNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getOwnerBookingsByState(1L, "CURRENT", new PaginationRequest(0, 5)));
    }

    @Test
    void getOwnerBookingsByState_whenAll() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = service.getOwnerBookingsByState(1L, "ALL", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1)).findByItemOwnerId(anyLong(), any(Pageable.class));
        assertThat(bookings.get(0).getItem().getDescription(), Matchers.equalTo("desc"));
    }

    @Test
    void getOwnerBookingsByState_whenByTime() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.getOwnerBookingsByState(1L, "CURRENT", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1))
                .findByItemOwnerIdCurrent(anyLong(), any(LocalDateTime.class), any(Pageable.class));

        ///////

        service.getOwnerBookingsByState(1L, "PAST", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));

        ///////

        service.getOwnerBookingsByState(1L, "FUTURE", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getOwnerBookingsByState_whenByStatus() {
        ArgumentCaptor<BookingStatus> statusCaptor = forClass(BookingStatus.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.getOwnerBookingsByState(1L, "WAITING", new PaginationRequest(0, 5));

        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatus(anyLong(),
                statusCaptor.capture(),
                any(Pageable.class));
        assertThat(statusCaptor.getValue(), Matchers.equalTo(BookingStatus.WAITING));
    }

}
