package ru.practicum.shareit;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "db.name=test2", webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AllArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShareItTests {
	private ItemService itemService;
	private BookingService bookingService;
	private ItemRequestService requestService;
	private UserRepository userRepository;
	private ItemRepository itemRepository;
	private BookingRepository bookingRepository;
	private ItemRequestRepository requestRepository;
	private CommentRepository commentRepository;

	@BeforeEach
	void setUp() {
		User user1 = new User(1L, "name1", "q@mail.ru");
		User user2 = new User(2L, "name2", "r@mailru");

		user1 = userRepository.save(user1);
		user2 = userRepository.save(user2);

		ItemRequest request1 = new ItemRequest(1L, "desc1", user2, LocalDateTime.now());
		ItemRequest request2 = new ItemRequest(2L, "desc2", user1, LocalDateTime.now());
		ItemRequest request3 = new ItemRequest(3L, "desc3", user2, LocalDateTime.now());

		request1 = requestRepository.save(request1);
		request2 = requestRepository.save(request2);
		request3 = requestRepository.save(request3);

		Item item1 = Item.builder()
				.id(1L)
				.owner(user1)
				.name("mane1")
				.description("desc1")
				.available(true)
				.request(request1)
				.build();
		Item item2 = Item.builder()
				.id(2L)
				.owner(user2)
				.name("mn2")
				.description("name2")
				.available(true)
				.request(request2)
				.build();
		Item item3 = Item.builder()
				.id(3L)
				.owner(user1)
				.request(request3)
				.name("name3")
				.description("desc3")
				.available(false)
				.build();

		item1 = itemRepository.save(item1);
		item2 = itemRepository.save(item2);
		item3 = itemRepository.save(item3);

		Booking booking1 = Booking.builder()
				.id(1L)
				.start(LocalDateTime.now().plusHours(40))
				.end(LocalDateTime.now().plusHours(50))
				.item(item3)
				.booker(user2)
				.status(BookingStatus.APPROVED)
				.build();
		Booking booking2 = Booking.builder()
				.id(2L)
				.start(LocalDateTime.now().minusHours(24))
				.end(LocalDateTime.now().minusHours(12))
				.status(BookingStatus.CANCELLED)
				.item(item2)
				.booker(user1)
				.build();
		Booking booking3 = Booking.builder()
				.id(3L)
				.start(LocalDateTime.now().minusHours(3))
				.end(LocalDateTime.now().plusHours(3))
				.status(BookingStatus.APPROVED)
				.item(item3)
				.booker(user2)
				.build();
		Booking booking4 = Booking.builder()
				.id(4L)
				.start(LocalDateTime.now().minusHours(12))
				.end(LocalDateTime.now().minusHours(6))
				.status(BookingStatus.WAITING)
				.item(item3)
				.booker(user2)
				.build();

		bookingRepository.save(booking1);
		bookingRepository.save(booking2);
		bookingRepository.save(booking3);
		bookingRepository.save(booking4);

		Comment comment1 = Comment.builder()
				.created(LocalDateTime.now())
				.text("text")
				.id(1L)
				.item(item1)
				.author(user2)
				.build();

		commentRepository.save(comment1);
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

	@Test
	void getBookingsByState() {
		List<BookingDto> bookings = bookingService.getBookingsByState(2L, "CURRENT", new PaginationRequest(0, 5));

		assertThat(bookings.size(), equalTo(1));
		assertThat(bookings.get(0).getId(), equalTo(3L));

		///

		bookings = bookingService.getBookingsByState(2L, "WAITING", new PaginationRequest(0, 5));
		assertThat(bookings.size(), equalTo(1));
		assertThat(bookings.get(0).getId(), equalTo(4L));

		///

		bookings = bookingService.getBookingsByState(1L, "REJECTED", new PaginationRequest(0, 5));
		assertThat(bookings.size(), equalTo(1));
		assertThat(bookings.get(0).getId(), equalTo(2L));
	}

	@Test
	void getOwnerBookingsByState() {
		List<BookingDto> bookings = bookingService.getOwnerBookingsByState(1L, "CURRENT", new PaginationRequest(0, 5));

		assertThat(bookings.size(), equalTo(1));
		assertThat(bookings.get(0).getId(), equalTo(3L));

		///

		bookings = bookingService.getOwnerBookingsByState(1L, "WAITING", new PaginationRequest(0, 5));
		assertThat(bookings.size(), equalTo(1));
		assertThat(bookings.get(0).getId(), equalTo(4L));

		///

		bookings = bookingService.getOwnerBookingsByState(2L, "REJECTED", new PaginationRequest(0, 5));
		assertThat(bookings.size(), equalTo(1));
		assertThat(bookings.get(0).getId(), equalTo(2L));
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
