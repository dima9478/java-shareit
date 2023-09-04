package ru.practicum.shareit.item;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl service;
    @Captor
    private ArgumentCaptor<Item> captor;
    private ItemDto dto;
    private User user;
    private User user2;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User(1L, "name", "email@mail.ru");
        user2 = new User(2L, "name2", "mail@yphoo.com");
        dto = ItemDto.builder()
                .name("phone")
                .description("desc")
                .available(true)
                .requestId(1L)
                .build();
        item = Item.builder()
                .id(1L)
                .owner(user)
                .available(false)
                .name("name")
                .description("desc")
                .build();

        request = new ItemRequest(1L, "desc", user2, LocalDateTime.now());
    }

    @Test
    void addItem_whenNoUser_thenThrowNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addItem(1L, dto));
    }

    @Test
    void addItem_whenWithRequestButNoRequest_thenThrowNotFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> service.addItem(1L, dto));
    }

    @Test
    void addItem_whenSuccessful_thenReturnDto() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.save(any(Item.class))).thenReturn(Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .owner(user)
                .request(request)
                .build()
        );

        ItemDto item = service.addItem(2L, dto);

        verify(itemRepository, times(1)).save(captor.capture());
        assertThat(item.getId(), Matchers.equalTo(1L));
        assertThat(captor.getValue().getOwner(), Matchers.equalTo(user2));
        assertThat(captor.getValue().getRequest(), Matchers.equalTo(request));
    }

    @Test
    void updateItem_whenNoItem_thenThrowNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateItem(1L, 1L, dto));
    }

    @Test
    void updateItem_whenOwnerNotEqualsUser_thenThrowAccessDenied() {
        Item item = Item.builder()
                .id(1L)
                .owner(user)
                .available(true)
                .name("name")
                .description("desc")
                .build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> service.updateItem(1L, 2L, dto));
    }

    @Test
    void updateItem_whenSuccessful_thenReturnDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item.toBuilder()
                .name("phone")
                .available(true)
                .build());

        ItemDto updatedItem = service.updateItem(1L, 1L, dto);

        assertThat(updatedItem.getAvailable(), Matchers.equalTo(true));
        assertThat(updatedItem.getName(), Matchers.equalTo("phone"));
    }

    @Test
    void getItemById_whenNoItem_thenThrowNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getItemById(1L, 1L));
    }

    @Test
    void getItemById_whenUserIsOwner_thenSetLastAndNextBooking() {
        Item item2 = Item.builder()
                .id(1L)
                .owner(user)
                .available(false)
                .name("name")
                .description("desc")
                .build();
        Booking booking1 = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.of(2020, 12, 23, 12, 30))
                .end(LocalDateTime.of(2023, 12, 23, 12, 34))
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item)
                .start(LocalDateTime.of(2025, 12, 23, 12, 30))
                .end(LocalDateTime.of(2026, 12, 23, 12, 34))
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item2));
        when(bookingRepository.findLastBookingsOfItem(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findNextBookingsOfItem(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking2));

        GetItemDto gotItem = service.getItemById(1L, 1L);

        assertThat(gotItem.getLastBooking(), Matchers.notNullValue());
        assertThat(gotItem.getNextBooking(), Matchers.notNullValue());
        assertThat(gotItem.getLastBooking().getId(), Matchers.equalTo(1L));
        assertThat(gotItem.getNextBooking().getId(), Matchers.equalTo(2L));
    }

    @Test
    void getItemById_whenComments_thenAddComments() {
        Comment comment = Comment.builder()
                .id(1L)
                .author(user2)
                .text("text")
                .created(LocalDateTime.now())
                .build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of(comment));

        GetItemDto gotItem = service.getItemById(1L, 2L);
        assertThat(gotItem.getComments().size(), Matchers.equalTo(1));
    }

    @Test
    void getItems_generalCheck() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.of(2020, 12, 23, 12, 30))
                .end(LocalDateTime.of(2023, 12, 23, 12, 34))
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item.toBuilder().id(2L).build())
                .start(LocalDateTime.of(2025, 12, 23, 12, 30))
                .end(LocalDateTime.of(2026, 12, 23, 12, 34))
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(item));
        when(bookingRepository.findLastBookingOfItems(any(List.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking1, booking2));
        when(bookingRepository.findNextBookingOfItems(any(List.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findByItemIdIn(any(List.class))).thenReturn(Collections.emptyList());

        List<GetItemDto> items = service.getItems(1L, new PaginationRequest(0, 5));

        assertThat(items.size(), Matchers.equalTo(1));
        assertThat(items.get(0).getLastBooking().getId(), Matchers.equalTo(1L));
        assertThat(items.get(0).getNextBooking(), Matchers.nullValue(null));
        assertThat(items.get(0).getComments(), Matchers.nullValue(null));
    }

    @Test
    void searchItems_whenBlankText_thenReturnEmptyList() {
        List<ItemDto> items = service.searchItems("", new PaginationRequest(0, 5));

        assertThat(items, Matchers.empty());
    }

    @Test
    void searchItems_whenSuccessful_thenReturnList() {
        when(itemRepository.searchItems("text", PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"))))
                .thenReturn(List.of(item));

        List<ItemDto> items = service.searchItems("text", new PaginationRequest(0, 1));

        assertThat(items.size(), Matchers.equalTo(1));
        assertThat(items.get(0).getId(), Matchers.equalTo(1L));
    }

    @Test
    void addComment_whenNoUser_thenThrowNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addComment(1L, 1L, new CreateCommentDto("text")));
    }

    @Test
    void addComment_whenNoItem_thenThrowNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addComment(1L, 1L, new CreateCommentDto("text")));
    }

    @Test
    void addComment_whenNoBookingWasMade_thenThrowBadRequest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.addComment(1L, 1L, new CreateCommentDto("text")));
    }

    @Test
    void addComment_whenCommentAlreadyExists_thenThrowBadRequest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.existsByAuthorIdAndItemId(1L, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.addComment(1L, 1L, new CreateCommentDto("text")));
    }

    @Test
    void addComment_whenSuccessful_thenReturnCommentDto() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.existsByAuthorIdAndItemId(1L, 1L)).thenReturn(false);
        when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder()
                .id(1L)
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build());

        CommentDto comment = service.addComment(1L, 1L, new CreateCommentDto("text"));

        assertThat(comment.getId(), Matchers.equalTo(1L));
    }
}
