package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        properties = "db.name=test3",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User user1NoId;
    private User user2NoId;
    private ItemDto itemDto;
    private BookingCreateRequestDto bookingCRD;


    @BeforeEach
    void setUp() {
        user1NoId = new User(null, "name", "email@mail.ru");
        user2NoId = new User(null, "anotherName", "email@yandex.ru");
        itemDto = new ItemDto(null, "name", "description", true, null);
        bookingCRD = new BookingCreateRequestDto(LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1), 1L);
    }

    @Test
    void decideBookingTest() {
        UserDto userDto1 = userService.create(user1NoId);
        UserDto userDto2 = userService.create(user2NoId);
        itemService.create(itemDto, userDto1.getId());
        BookingDto bookingDto = bookingService.create(bookingCRD, userDto2.getId());

        BookingDto bookingDto1 = bookingService.decideBooking(bookingDto.getId(), userDto1.getId(), true);
        Item item = itemRepository.findById(1L).get();
        User user = userRepository.findById(2L).get();

        assertThat(bookingDto1.getId(), notNullValue());
        assertThat(bookingDto1.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDto1.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDto1.getItem(), equalTo(item));
        assertThat(bookingDto1.getBooker(), equalTo(user));
        assertThat(bookingDto1.getStatus(), equalTo(BookingStatus.APPROVED));
    }
}
