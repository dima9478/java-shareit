package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ItemRequestMapperTest {
    @Test
    void toItemRequest() {
        User user = new User(1L, "name", "e@mail.com");

        ItemRequest request = ItemRequestMapper.toItemRequest(new CreateRequestDto("desc23"), user);

        assertThat(request.getId(), nullValue());
        assertThat(request.getDescription(), equalTo("desc23"));
        assertThat(request.getCreated(), notNullValue());
        assertThat(request.getRequestor(), equalTo(user));
    }

    @Test
    void toDto() {
        LocalDateTime created = LocalDateTime.of(2014, 12, 23, 11, 34);
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .created(created)
                .description("desc45")
                .requestor(new User(1L, "name", "e@mail.com"))
                .build();
        List<Item> items = List.of(Item.builder()
                .id(23L)
                .name("iname")
                .description("idesc")
                .available(true)
                .request(request)
                .build());
        Item item = items.get(0);

        ItemRequestDto dto = ItemRequestMapper.toDto(request, items);
        GetItemRequestItemDto itemDto = dto.getItems().get(0);


        assertThat(dto.getId(), equalTo(request.getId()));
        assertThat(dto.getDescription(), equalTo(request.getDescription()));
        assertThat(dto.getCreated(), equalTo(created));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getAvailable(), equalTo(item.isAvailable()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getRequestId(), equalTo(item.getRequest().getId()));
    }
}
