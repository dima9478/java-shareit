package ru.practicum.shareit;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ApplicationJsonTest {
    private JacksonTester<CommentDto> commentTester;
    private JacksonTester<BookingDto> bookingTester;
    private JacksonTester<BookingCreateDto> bookingCreateTester;
    private JacksonTester<ItemRequestDto> requestTester;

    @SneakyThrows
    @Test
    void testCommentDto() {
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .id(1L)
                .created(LocalDateTime.of(2012, 12, 23, 12, 23))
                .authorName("name")
                .build();

        JsonContent<CommentDto> result = commentTester.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2012-12-23T12:23:00");
    }

    @SneakyThrows
    @Test
    void testBookingDto() {
        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.of(2012, 12, 23, 12, 23))
                .end(LocalDateTime.of(2012, 12, 23, 12, 56))
                .id(1L)
                .build();

        JsonContent<BookingDto> result = bookingTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2012-12-23T12:23:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2012-12-23T12:56:00");
    }

    @SneakyThrows
    @Test
    void testBookingCreateDto() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .start(LocalDateTime.of(2012, 12, 23, 12, 23))
                .end(LocalDateTime.of(2012, 12, 23, 12, 56))
                .build();

        JsonContent<BookingCreateDto> result = bookingCreateTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2012-12-23T12:23:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2012-12-23T12:56:00");
    }

    @SneakyThrows
    @Test
    void testItemRequestDto() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .created(LocalDateTime.of(2014, 11, 23, 14, 45))
                .description("desc")
                .build();

        JsonContent<ItemRequestDto> result = requestTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2014-11-23T14:45:00");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}
