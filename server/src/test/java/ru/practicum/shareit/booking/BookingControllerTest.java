package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService service;
    @InjectMocks
    private BookingController controller;
    private final BookingDto dto = BookingDto.builder()
            .id(1L)
            .item(ItemDto.builder()
                    .id(1L)
                    .available(true)
                    .name("name")
                    .build())
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(12))
            .booker(UserDto.builder()
                    .id(2L)
                    .email("email")
                    .name("uname")
                    .build())
            .status(BookingStatus.APPROVED)
            .build();

    @Test
    void addBooking() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(12))
                .itemId(1L)
                .build();
        when(service.addBooking(createDto, 1L)).thenReturn(dto);

        assertThat(controller.addBooking(1L, createDto), equalTo(dto));
    }

    @Test
    void changeBookingStatus() {
        when(service.finalizeBookingStatus(1L, 1L, true)).thenReturn(dto);

        assertThat(controller.changeBookingStatus(1L, 1L, true), equalTo(dto));
    }

    @Test
    void getBooking() {
        when(service.getBookingById(1L, 1L)).thenReturn(dto);

        assertThat(controller.getBooking(1L, 1L), equalTo(dto));
    }

    @Test
    void getBookingByState() {
        when(service.getBookingsByState(1L, BookingFilterState.CURRENT, new PaginationRequest(0, 10)))
                .thenReturn(List.of(dto));

        assertThat(controller.getBookingByState(1L, BookingFilterState.CURRENT, 0, 10), equalTo(List.of(dto)));
    }

    @Test
    void getOwnerBookingByState() {
        when(service.getOwnerBookingsByState(1L, BookingFilterState.CURRENT, new PaginationRequest(0, 10)))
                .thenReturn(List.of(dto));

        assertThat(controller.getOwnerBookingByState(1L, BookingFilterState.CURRENT, 0, 10), equalTo(List.of(dto)));
    }
}
