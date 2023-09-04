package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(
        properties = "db.name=test2",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private User user1NoId;
    private User user2NoId;
    private ItemDto itemNoId;
    //private Item item;

    @BeforeEach
    void setUp() {
        user1NoId = new User(null, "name", "email@mail.ru");
        user2NoId = new User(null, "anotherName", "email@yandex.ru");
        //item = new Item(null, "name", "description", true, user1NoId, null);
        itemNoId = new ItemDto(null, "name", "description", true, null);
    }

    @Test
    void getByIdTest() throws InterruptedException {
        UserDto userDto1 = userService.create(user1NoId);
        UserDto userDto2 = userService.create(user2NoId);
        ItemDto itemDto = itemService.create(itemNoId, userDto1.getId());

        bookingService.create(new BookingCreateRequestDto(LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2), itemDto.getId()), userDto2.getId());
        TimeUnit.SECONDS.sleep(3);

        bookingService.create(new BookingCreateRequestDto(LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(60), itemDto.getId()), userDto2.getId());
        BookingDto bookingDtoCurrent = bookingService.decideBooking(2L, 1L, true);
        TimeUnit.SECONDS.sleep(1);

        bookingService.create(new BookingCreateRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                itemDto.getId()), userDto2.getId());
        BookingDto bookingDtoFuture = bookingService.decideBooking(3L, 1L, true);

        ItemDtoResponse itemDtoResponse = itemService.getById(1L, 1L);

        ItemDtoResponse expectedItem = new ItemDtoResponse(1L, "name", "description", true,
                BookingMapper.toBookingForItemDtoResponse(bookingDtoCurrent),
                BookingMapper.toBookingForItemDtoResponse(bookingDtoFuture), List.of());

        assertThat(expectedItem, equalTo(itemDtoResponse));
    }
}
