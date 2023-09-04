package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService service;
    @InjectMocks
    ItemRequestController controller;
    private final GetItemRequestItemDto itemDto = GetItemRequestItemDto.builder()
            .requestId(1L)
            .id(1L)
            .available(true)
            .description("i_desc")
            .build();
    private final ItemRequestDto dto = ItemRequestDto.builder()
            .id(1L)
            .description("desc1")
            .created(LocalDateTime.now())
            .items(List.of(itemDto))
            .build();

    @Test
    void addRequest() {
        when(service.addRequest(1L, new CreateRequestDto("desc1"))).thenReturn(dto);

        assertThat(controller.addRequest(1L, new CreateRequestDto("desc1")), equalTo(dto));
    }

    @Test
    void getUserRequests() {
        when(service.getUserRequests(1L)).thenReturn(List.of(dto));

        assertThat(controller.getUserRequests(1L), equalTo(List.of(dto)));
    }

    @Test
    void getAllRequests() {
        when(service.getAllRequests(2L, new PaginationRequest(0, 10))).thenReturn(List.of(dto));

        assertThat(controller.getAllRequests(2L, 0, 10), equalTo(List.of(dto)));
    }

    @Test
    void getRequest() {
        when(service.getRequest(1L, 1L)).thenReturn(dto);

        assertThat(controller.getRequest(1L, 1L), equalTo(dto));
    }
}
