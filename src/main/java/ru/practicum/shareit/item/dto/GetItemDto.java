package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ItemBookingDto;

import java.util.List;


@Data
@Builder
public class GetItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private List<CommentDto> comments;
}
