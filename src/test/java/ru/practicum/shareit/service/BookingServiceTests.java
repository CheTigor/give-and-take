package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    BookingService bookingService;
    Booking booking;
    Item item;
    User user;
    User user2;
    BookingCreateRequestDto bookingCRD;
    List<Booking> bookings;

    @BeforeEach
    void setUp() {
        user = new User(1L, "name", "email@mail.ru");
        user2 = new User(2L, "anotherName", "email@yandex.ru");
        item = new Item(1L, "name", "description", true, user, null);
        booking = new Booking(1L, LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1), item, user2,
                BookingStatus.APPROVED);
        bookingCRD = new BookingCreateRequestDto(booking.getStart(), booking.getEnd(), booking.getItem().getId());

        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        bookings = new ArrayList<>();
        bookings.add(booking);
    }

    @Test
    void createBooking() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.create(bookingCRD, user2.getId());

        Assertions.assertEquals(BookingMapper.toBookingDto(booking), bookingDto);
    }

    @Test
    void createBookingWithUserNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () ->
                bookingService.create(bookingCRD, user2.getId()));
    }

    @Test
    void createBookingWithItemNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () ->
                bookingService.create(bookingCRD, user2.getId()));
    }

    @Test
    void createBookingWithDateValidationException() {
        bookingCRD.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Assertions.assertThrows(DateValidationException.class, () ->
                bookingService.create(bookingCRD, user2.getId()));
    }

    @Test
    void createBookingWithAlreadyIsOwnerException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Assertions.assertThrows(AlreadyIsOwnerException.class, () ->
                bookingService.create(bookingCRD, user.getId()));
    }

    @Test
    void createBookingWithNotAvailableException() {
        item.setAvailable(false);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Assertions.assertThrows(NotAvailableException.class, () ->
                bookingService.create(bookingCRD, user2.getId()));
    }

    @Test
    void getBookingById() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getById(1L, 2L);

        Assertions.assertEquals(BookingMapper.toBookingDto(booking), bookingDto);
    }

    @Test
    void getBookingByIdWithBookingNotFoundException() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BookingNotFoundException.class, () ->
                bookingService.getById(1L, 2L));
    }

    @Test
    void getBookingByIdWithUserNotFoundException() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(UserNotFoundException.class, () ->
                bookingService.getById(1L, 20L));
    }

    @Test
    void getAllBookings() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findByBooker_idOrderByStartDesc(anyLong(), any())).thenReturn(
                new PageImpl<>(bookings));
        List<BookingDto> bookingAllDtos = bookingService.getAll("ALL", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingAllDtos);

        Mockito.when(bookingRepository.findByBooker_idAndEndIsAfterAndStartIsBeforeOrderByStartDesc(
                anyLong(), any(), any(), any())).thenReturn(new PageImpl<>(bookings));
        List<BookingDto> bookingCurrentDtos = bookingService.getAll("CURRENT", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingCurrentDtos);

        Mockito.when(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));
        List<BookingDto> bookingPastDtos = bookingService.getAll("PAST", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingPastDtos);

        Mockito.when(bookingRepository.findByBooker_idAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));
        List<BookingDto> bookingFutureDtos = bookingService.getAll("FUTURE", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingFutureDtos);

        Mockito.when(bookingRepository.findByBooker_idAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));
        List<BookingDto> bookingWaitingDtos = bookingService.getAll("WAITING", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingWaitingDtos);

        Mockito.when(bookingRepository.findByBooker_idAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));
        List<BookingDto> bookingRejectedDtos = bookingService.getAll("REJECTED", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingRejectedDtos);
    }

    @Test
    void getAllBookingsWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () ->
                bookingService.getAll("ALL", 1L, 0, 20));
    }

    @Test
    void getAllBookingsWithIllegalArgumentException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookingService.getAll("UNSUPPORTED", 1L, 0, 20));
    }

    @Test
    void getOwnerAll() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findByItem_owner_idOrderByStartDesc(anyLong(), any())).thenReturn(
                new PageImpl<>(bookings));
        List<BookingDto> bookingAllDtos = bookingService.getOwnerAll("ALL", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingAllDtos);

        Mockito.when(bookingRepository.findByItem_owner_idAndEndIsAfterAndStartIsBeforeOrderByStartDesc(anyLong(), any(),
                any(), any())).thenReturn(new PageImpl<>(bookings));
        List<BookingDto> bookingCurrentDtos = bookingService.getOwnerAll("CURRENT", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingCurrentDtos);

        Mockito.when(bookingRepository.findByItem_owner_idAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));
        List<BookingDto> bookingPastDtos = bookingService.getOwnerAll("PAST", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingPastDtos);

        Mockito.when(bookingRepository.findByItem_owner_idAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));
        List<BookingDto> bookingFutureDtos = bookingService.getOwnerAll("FUTURE", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingFutureDtos);

        Mockito.when(bookingRepository.findByItem_owner_idAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(
                new PageImpl<>(bookings));
        List<BookingDto> bookingWaitingDtos = bookingService.getOwnerAll("WAITING", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingWaitingDtos);

        Mockito.when(bookingRepository.findByItem_owner_idAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(
                new PageImpl<>(bookings));
        List<BookingDto> bookingRejectedDtos = bookingService.getOwnerAll("REJECTED", 1L, 0, 20);
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)), bookingRejectedDtos);
    }

    @Test
    void getOwnerAllWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () ->
                bookingService.getOwnerAll("ALL", 1L, 0, 20));
    }

    @Test
    void getOwnerAllWithIllegalArgumentException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookingService.getOwnerAll("UNSUPPORTED", 1L, 0, 20));
    }

    @Test
    void decideBookingStatusIsApproved() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(BadRequestException.class, () ->
                bookingService.decideBooking(1L, 1L, true));
    }

    @Test
    void decideBookingWithBookingNotFoundException() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BookingNotFoundException.class, () ->
                bookingService.decideBooking(1L, 1L, true));
    }

    @Test
    void decideBookingWithMismatchUserIdException() {
        Booking bookingWait = new Booking(1L, LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1), item, user2,
                BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWait));

        Assertions.assertThrows(MismatchUserIdException.class, () ->
                bookingService.decideBooking(1L, 20L, true));
    }

    @Test
    void decideBookingStatusIsWaiting() {
        Booking bookingWait = new Booking(1L, LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1), item, user2,
                BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWait));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.decideBooking(1L, 1L, true);

        Assertions.assertEquals(BookingMapper.toBookingDto(booking), bookingDto);
    }
}
