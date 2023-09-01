package ru.practicum.shareit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = "db.name=test2")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor
public class UserRepositoryIT {
    @Autowired
    private UserRepository repository;

    @Test
    void generalCheck() {
        User user = repository.save(User.builder()
                .name("name")
                .email("email")
                .build());

        assertTrue(repository.findById(user.getId()).isPresent());
        assertEquals(repository.findAll().size(), 1);
        repository.deleteById(user.getId());
        assertTrue(repository.findById(user.getId()).isEmpty());
    }
}
