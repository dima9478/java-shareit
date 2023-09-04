package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DataJpaTest(properties = "db.name=test2")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor
public class ItemRepositoryIT {
    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User(1L, "Igor", "e@mail.ru");
        User user2 = new User(2L, "Petr", "p@goole.com");


        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        ItemRequest request1 = new ItemRequest(1L, "desc1", user2, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(2L, "desc2", user1, LocalDateTime.now());

        request1 = requestRepository.save(request1);
        request2 = requestRepository.save(request2);

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
                .name("name3")
                .description("desc3")
                .available(false)
                .build();

        repository.save(item1);
        repository.save(item2);
        repository.save(item3);
    }

    @Test
    void findAllByOwnerId_whenAll_thenReturn2() {
        List<Item> items = repository.findAllByOwnerId(1L, PageRequest.of(0, 5));

        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getOwner().getId(), equalTo(1L));
        assertThat(items.get(0).getOwner().getId(), equalTo(1L));
    }

    @Test
    void findAllByOwnerId_whenSize1_thenReturn1() {
        List<Item> items = repository.findAllByOwnerId(1L, PageRequest.of(0, 1));

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getOwner().getId(), equalTo(1L));
    }

    @Test
    void searchItems_whenContainsName_thenReturn2() {
        List<Item> items = repository.searchItems("name", PageRequest.of(0, 5));

        assertThat(items.size(), equalTo(2));
        assertTrue("Don't contains substring", items.get(0).getName().contains("name") ||
                items.get(0).getDescription().contains("name"));
        assertTrue("Don't contains substring", items.get(1).getName().contains("name") ||
                items.get(1).getDescription().contains("name"));
    }

    @Test
    void searchItems_whenNameEqualsNameSize1_thenReturn1() {
        List<Item> items = repository.searchItems("name", PageRequest.of(0, 1));

        assertThat(items.size(), equalTo(1));
        assertTrue("Don't contains substring", items.get(0).getName().contains("name") ||
                items.get(0).getDescription().contains("name"));
    }

    @Test
    void searchItems_whenNoMatches_thenReturn0() {
        List<Item> items = repository.searchItems("oper", PageRequest.of(0, 1));

        assertThat(items.size(), equalTo(0));
    }

    @Test
    void findAllByRequestIdIn_whenAllRequests_thenReturn2() {
        List<Item> items = repository.findAllByRequestIdIn(Set.of(1L, 2L));

        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getRequest().getId(), anyOf(equalTo(1L), equalTo(2L)));
        assertThat(items.get(1).getRequest().getId(), anyOf(equalTo(1L), equalTo(2L)));
    }

    @Test
    void findAllByRequestIdIn_whenNoRequest_thenReturn0() {
        List<Item> items = repository.findAllByRequestIdIn(Set.of(5L));

        assertThat(items.size(), equalTo(0));
    }

    @Test
    void findAllByRequestId_whenRequest1_thenReturn1() {
        List<Item> items = repository.findAllByRequestId(1L);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getRequest().getId(), equalTo(1L));
    }

    @Test
    void findAllByRequestId_whenNoRequest_thenReturn0() {
        List<Item> items = repository.findAllByRequestId(7L);

        assertThat(items.size(), equalTo(0));
    }
}
