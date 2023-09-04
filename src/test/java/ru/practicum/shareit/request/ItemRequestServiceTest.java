package ru.practicum.shareit.request;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl service;
    private final User user = new User(1L, "name", "mail.com");
    private final User user2 = new User(2L, "name2", "mw@g.com");
    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("r_desc")
            .requestor(user2)
            .created(LocalDateTime.now())
            .build();
    private final Item item1 = Item.builder()
            .id(1L)
            .available(true)
            .owner(user)
            .name("name")
            .description("i_desc")
            .request(request)
            .build();

    @Test
    void addRequest_whenNoUser_thenThrowNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addRequest(2L, new CreateRequestDto("desc")));
    }

    @Test
    void addRequest_whenSuccessful_thenReturnDto() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto dto = service.addRequest(2L, new CreateRequestDto("r_desc"));

        assertThat(dto.getItems(), Matchers.empty());
        assertThat(dto.getId(), Matchers.equalTo(1L));
        assertThat(dto.getDescription(), Matchers.equalTo("r_desc"));
    }

    @Test
    void getUserRequests_whenNoUser_thenThrowNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getUserRequests(2L));
    }

    @Test
    void getUserRequests_whenSuccessful_thenReturnDtoWithItemsList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findAllByRequestIdIn(any(Set.class))).thenReturn(List.of(item1));
        when(requestRepository.findAllByRequestorId(2L, Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requests = service.getUserRequests(2L);

        assertThat(requests.size(), Matchers.equalTo(1));
        assertThat(requests.get(0).getId(), Matchers.equalTo(1L));
        assertThat(requests.get(0).getItems().size(), Matchers.equalTo(1));
        assertThat(requests.get(0).getItems().get(0).getId(), Matchers.equalTo(1L));
    }

    @Test
    void getAllRequests_whenNoUser_thenThrowNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getAllRequests(2L, new PaginationRequest(0, 10)));
    }

    @Test
    void getAllRequests_whenSuccessful_thenReturnDtoWithItemsList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdNot(anyLong(), any(Pageable.class))).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(any(Set.class))).thenReturn(List.of(item1));

        List<ItemRequestDto> requests = service.getAllRequests(1L, new PaginationRequest(0, 10));

        assertThat(requests.size(), Matchers.equalTo(1));
        assertThat(requests.get(0).getId(), Matchers.equalTo(1L));
        assertThat(requests.get(0).getItems().size(), Matchers.equalTo(1));
        assertThat(requests.get(0).getItems().get(0).getId(), Matchers.equalTo(1L));
    }

    @Test
    void getRequest_whenNoUser_thenThrowNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getRequest(2L, 1L));
    }

    @Test
    void getRequest_whenNoRequest_thenThrowNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getRequest(2L, 1L));
    }

    @Test
    void getRequest_whenSuccessful_thenReturnDto() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item1));

        ItemRequestDto dto = service.getRequest(2L, 1L);

        assertThat(dto.getId(), Matchers.equalTo(1L));
        assertThat(dto.getItems().size(), Matchers.equalTo(1));
        assertThat(dto.getItems().get(0).getName(), Matchers.equalTo("name"));
    }
}
