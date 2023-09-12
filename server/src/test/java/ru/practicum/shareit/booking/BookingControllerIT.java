package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerIT {
    @MockBean
    private BookingService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
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
    private final String userHeader = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void addBooking_whenNotFoundException_thenReturn404() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.now().plusHours(12), LocalDateTime.now());
        when(service.addBooking(
                bookingCreateDto,
                1L)).thenThrow(NotFoundException.class);

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addBooking_whenSuccessful_returnDto() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.now().plusHours(12), LocalDateTime.now());
        when(service.addBooking(
                bookingCreateDto,
                1L)).thenReturn(dto);

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())))
                .andExpect(jsonPath("$.item.name", is("name")))
                .andExpect(jsonPath("$.booker.name", is("uname")));
    }

    @SneakyThrows
    @Test
    void changeBookingStatus_whenNotFound_thenReturn404() {
        when(service.finalizeBookingStatus(
                1L, 2L, true)).thenThrow(NotFoundException.class);

        mvc.perform(patch("/bookings/{id}?approved=true", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 2L))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void changeBookingStatus_whenIllegalArgument_thenReturn400() {
        when(service.finalizeBookingStatus(
                1L, 2L, true)).thenThrow(IllegalArgumentException.class);

        mvc.perform(patch("/bookings/{id}?approved=true", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 2L))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void changeBookingStatus_whenSuccessful_returnDto() {
        when(service.finalizeBookingStatus(1L, 2L, true)).thenReturn(dto);

        mvc.perform(patch("/bookings/{id}?approved=true", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())))
                .andExpect(jsonPath("$.item.name", is("name")))
                .andExpect(jsonPath("$.booker.name", is("uname")));
    }

    @SneakyThrows
    @Test
    void getBooking_whenNotFound_thenReturn404() {
        when(service.getBookingById(1L, 1L)).thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getBooking_whenSuccessful_theReturnDto() {
        when(service.getBookingById(1L, 1L)).thenReturn(dto);

        mvc.perform(get("/bookings/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())))
                .andExpect(jsonPath("$.item.name", is("name")))
                .andExpect(jsonPath("$.booker.name", is("uname")));
    }

    @SneakyThrows
    @Test
    void getBookingByState_whenIllegalArgument_thenReturn400() {
        when(service.getBookingsByState(1L, BookingFilterState.ALL, new PaginationRequest(0, 10)))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookingByState_whenSuccessful_thenReturnDtoList() {
        when(service.getBookingsByState(1L, BookingFilterState.CURRENT, new PaginationRequest(0, 2)))
                .thenReturn(List.of(dto));

        mvc.perform(get("/bookings?state=CURRENT&from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.name())))
                .andExpect(jsonPath("$[0].item.name", is("name")))
                .andExpect(jsonPath("$[0].booker.name", is("uname")));
    }

    @SneakyThrows
    @Test
    void getOwnerBookingByState_whenIllegalArgument_thenReturn400() {
        when(service.getOwnerBookingsByState(1L, BookingFilterState.ALL, new PaginationRequest(0, 10)))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getOwnerBookingByState_whenSuccessful_thenReturnDtoList() {
        when(service.getOwnerBookingsByState(1L, BookingFilterState.CURRENT, new PaginationRequest(0, 2)))
                .thenReturn(List.of(dto));

        mvc.perform(get("/bookings/owner?state=CURRENT&from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.name())))
                .andExpect(jsonPath("$[0].item.name", is("name")))
                .andExpect(jsonPath("$[0].booker.name", is("uname")));
    }
}
