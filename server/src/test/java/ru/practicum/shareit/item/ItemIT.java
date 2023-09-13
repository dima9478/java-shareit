package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.ShareItTests;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ItemIT extends ShareItTests {
    @Autowired
    protected ItemIT(ItemService itemService, BookingService bookingService, ItemRequestService requestService, UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository, ItemRequestRepository requestRepository, CommentRepository commentRepository) {
        super(itemService, bookingService, requestService, userRepository, itemRepository, bookingRepository, requestRepository, commentRepository);
    }

    @Test
    void getItems() {
        List<GetItemDto> items = itemService.getItems(1L, new PaginationRequest(0, 5));

        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getId(), equalTo(1L));
        assertThat(items.get(0).getComments().size(), equalTo(1));
        assertThat(items.get(0).getNextBooking(), nullValue());
        assertThat(items.get(0).getLastBooking(), nullValue());
        assertThat(items.get(1).getId(), equalTo(3L));
        assertThat(items.get(1).getNextBooking(), notNullValue());

    }
}
