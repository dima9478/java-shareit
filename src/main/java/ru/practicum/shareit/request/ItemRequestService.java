package ru.practicum.shareit.request;

import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(long userId, @Valid CreateRequestDto dto);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getAllRequests(long userId, @Valid PaginationRequest pagRequest);

    ItemRequestDto getRequest(long userId, long requestId);
}
