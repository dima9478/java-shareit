package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.ShareItTests;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class ItemRequestIT extends ShareItTests {
    @Autowired
    protected ItemRequestIT(ItemService itemService, BookingService bookingService, ItemRequestService requestService, UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository, ItemRequestRepository requestRepository, CommentRepository commentRepository) {
        super(itemService, bookingService, requestService, userRepository, itemRepository, bookingRepository, requestRepository, commentRepository);
    }

    @Test
    void getUserRequests() {
        List<ItemRequestDto> requests = requestService.getUserRequests(2L);

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getItems().size(), equalTo(1));
        assertThat(requests.get(1).getItems().size(), equalTo(1));
        assertThat(
                requests.stream().map(ItemRequestDto::getId).collect(Collectors.toList()),
                containsInAnyOrder(equalTo(1L), equalTo(3L))
        );
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDto> requests = requestService.getAllRequests(1L, new PaginationRequest(0, 10));

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getItems().size(), equalTo(1));
        assertThat(requests.get(1).getItems().size(), equalTo(1));
        assertThat(
                requests.stream().map(ItemRequestDto::getId).collect(Collectors.toList()),
                containsInAnyOrder(equalTo(1L), equalTo(3L))
        );
    }
}
