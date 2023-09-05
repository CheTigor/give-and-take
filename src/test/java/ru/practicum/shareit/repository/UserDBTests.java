package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
public class UserDBTests {

    @Autowired
    private UserRepository repository;

    @Test
    void saveUser() {
        User user = new User(null, "name", "email@mail.com");

        Assertions.assertNull(user.getId());
        repository.save(user);
        Assertions.assertNotNull(user.getId());
    }
}
