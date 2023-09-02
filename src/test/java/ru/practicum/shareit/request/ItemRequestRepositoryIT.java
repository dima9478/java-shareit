package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest(properties = "db.name=test2")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryIT {
    private ItemRequestRepository requestRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User(1L, "Igor", "e@mail.ru");
        User user2 = new User(2L, "Petr", "p@goole.com");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        ItemRequest request1 = new ItemRequest(1L, "desc1", user2, LocalDateTime.of(2015, 12, 23, 12, 34));
        ItemRequest request2 = new ItemRequest(2L, "desc2", user1, LocalDateTime.of(2016, 12, 23, 12, 34));
        ItemRequest request3 = new ItemRequest(3L, "desc3", user2, LocalDateTime.of(2017, 12, 23, 12, 34));

        request1 = requestRepository.save(request1);
        request2 = requestRepository.save(request2);
        request3 = requestRepository.save(request3);

        Item item1 = Item.builder()
                .id(1L)
                .owner(user1)
                .name("mane1")
                .request(request1)
                .description("desc1")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .owner(user2)
                .name("mn2")
                .request(request2)
                .description("name2")
                .available(true)
                .build();
        Item item3 = Item.builder()
                .id(3L)
                .owner(user1)
                .name("name3")
                .request(request3)
                .description("desc3")
                .available(false)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(
                2L,
                Sort.by(Sort.Direction.DESC, "created")
        );

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getId(), equalTo(3L));
        assertThat(requests.get(1).getId(), equalTo(1L));
    }

    @Test
    void findAllByRequestorIdNot() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(2L, PageRequest.of(0, 10));

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getId(), equalTo(2L));

        ///

        requests = requestRepository.findAllByRequestorIdNot(1L, PageRequest.of(0, 1));
        assertThat(requests.size(), equalTo(1));
    }
}
