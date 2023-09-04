package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@DataJpaTest
public class ItemDBTests {

    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void searchItemByQuery() {
        User user = new User(1L, "name", "email@mail.ru");
        Item item1 = new Item(null, "name", "description", true, user, null);
        Item item2 = new Item(null, "name123", "description", true, user, null);
        Item item3 = new Item(null, "name", "description123", true, user, null);
        userRepository.save(user);
        repository.save(item1);
        repository.save(item2);
        repository.save(item3);
        List<Item> items = repository.findByQueryIgnoreCase(
                "123", PageRequest.of(0, 20)).toList();
        Assertions.assertEquals(items, List.of(item2, item3));
    }
}
