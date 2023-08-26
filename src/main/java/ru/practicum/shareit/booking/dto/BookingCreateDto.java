package ru.practicum.shareit.booking.dto;


import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
public class BookingCreateDto {
    @NotNull
    private Long itemId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
}
