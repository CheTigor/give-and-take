package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED )
public class ItemRequestDBTests {

    @Autowired
    private ItemRequestRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void test() {
        final User user = new User(null, "name", "email@mail.ru");
        userRepository.save(user);
        final ItemRequest request = new ItemRequest(null, "description", LocalDateTime.now(), user);
        repository.save(request);


        Assertions.assertEquals(List.of(repository.findById(1L).get()),
                repository.findNotByRequester_Id(2L, PageRequest.of(0, 20)));
    }

}
