package ru.practicum.shareit.item.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(@NonNull Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public static GetItemDto toItemDto(@NonNull Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        return GetItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .lastBooking(BookingMapper.toItemBookingDto(lastBooking))
                .nextBooking(BookingMapper.toItemBookingDto(nextBooking))
                .comments(comments == null ? null : comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()))
                .build();
    }

    public static Item toItem(@NonNull ItemDto itemDto, @NonNull User user, ItemRequest itemRequest) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .request(itemRequest)
                .build();
    }
}
