package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
public class BookingDBTests {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void saveBooking() {
        User user = new User(null, "name", "email@mail.ru");
        Item item = new Item(null, "name", "description", true, user, null);
        User user1 = userRepository.save(user);
        Item item1 = itemRepository.save(item);

        Booking booking = new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item1, user1,
                BookingStatus.WAITING);
        Assertions.assertNull(booking.getId());

        bookingRepository.save(booking);
        Assertions.assertNotNull(booking.getId());
    }
}
