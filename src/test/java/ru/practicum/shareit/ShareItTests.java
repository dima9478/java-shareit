package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@SpringBootTest(properties = "db.name=test2", webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AllArgsConstructor(onConstructor_ = @Autowired, access = AccessLevel.PROTECTED)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ShareItTests {
	protected ItemService itemService;
	protected BookingService bookingService;
	protected ItemRequestService requestService;
	protected UserRepository userRepository;
	protected ItemRepository itemRepository;
	protected BookingRepository bookingRepository;
	protected ItemRequestRepository requestRepository;
	protected CommentRepository commentRepository;

	@BeforeEach
	protected void setUp() {
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
}
