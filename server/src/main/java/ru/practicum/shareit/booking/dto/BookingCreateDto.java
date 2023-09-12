package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
public class BookingCreateDto {
    private Long itemId;
    private LocalDateTime end;
    private LocalDateTime start;
}
