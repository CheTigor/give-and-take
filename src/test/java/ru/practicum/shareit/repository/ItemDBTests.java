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
        final User userNoId1 = new User(null, "name", "email@mail.ru");
        final User user1 = userRepository.save(userNoId1);
        final Item itemNoId1 = new Item(null, "name", "description", true, user1, null);
        final Item itemNoId2 = new Item(null, "name123", "description", true, user1, null);
        final Item itemNoId3 = new Item(null, "name", "description123", false, user1, null);
        final Item item1 = repository.save(itemNoId1);
        final Item item2 = repository.save(itemNoId2);
        final Item item3 = repository.save(itemNoId3);
        itemNoId2.setId(2L);
        List<Item> items = repository.findByQueryIgnoreCase("123", PageRequest.of(0, 20)).toList();
        Assertions.assertEquals(items, List.of(item2));
    }
}
