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
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void saveBooking() {
        Item item = new Item(1L, "name", "description", true, 1L, null);
        User user = new User(1L, "name", "email@mail.ru");
        Booking booking = new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user,
                BookingStatus.WAITING);

        userRepository.save(user);
        itemRepository.save(item);

        Assertions.assertNull(booking.getId());
        bookingRepository.save(booking);
        Assertions.assertNotNull(booking.getId());
    }
}
