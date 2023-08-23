package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto create(BookingCreateRequestDto bookingReq, Long bookerId) {
        final Item item;
        final User user;
        try {
            user = userRepository.findById(bookerId).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException(String.format("User с id: %d не найден", bookerId));
        }
        try {
            item = itemRepository.findById(bookingReq.getItemId()).get();
        } catch (NoSuchElementException e) {
            throw new ItemNotFoundException(String.format("Item с id: %d не найден", bookingReq.getItemId()));
        }
        if (!dateValidation(bookingReq)) {
            throw new DateValidationException("Неверный формат даты");
        }
        if (item.getOwner().equals(bookerId)) {
            throw new AlreadyIsOwnerException(String.format("Booker уже является владельцем вещи, bookerId: %d, itemId: %d",
                    bookerId, item.getId()));
        }
        if (item.getAvailable().equals(true)) {
            final Booking bookingWithId = bookingRepository.save(BookingMapper.toBooking(bookingReq, user, item));
            log.debug("Успешное создание booking: {}", bookingWithId);
            log.debug("Item больше недоступен для бронирования, itemId: {}", item.getId());
            return BookingMapper.toBookingDto(bookingWithId);
        } else {
            throw new NotAvailableException("Item не доступен для бронирования в это время");
        }
    }

    public BookingDto getById(Long bookingId, Long userId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException(String.format("BookingId не найден в базе, bookingId: %d", bookingId));
        }
        final Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new UserNotFoundException(String.format(
                    "User не является автором бронирования или владельцем вещи или не существует, userId: %d", userId));
        }
    }

    public List<BookingDto> getAll(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Userid не существует в базе, userId: %d", userId));
        }
        try {
            BookingState stateLC = BookingState.valueOf(state);
            switch (stateLC) {
                case ALL:
                    return bookingRepository.findByBooker_idOrderByStartDesc(userId).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findByBooker_idAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId,
                                    LocalDateTime.now(), LocalDateTime.now()).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findByBooker_idAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                default:
                    throw new UnsupportedStateException(String.format("Unknown state: %s", state));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(state);
        }
    }

    @Override
    public List<BookingDto> getOwnerAll(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("User с id: %d не найден", userId));
        }
        try {
            BookingState stateLC = BookingState.valueOf(state);
            switch (stateLC) {
                case ALL:
                    return bookingRepository.findByItem_ownerOrderByStartDesc(userId).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findByItem_ownerAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId,
                                    LocalDateTime.now(), LocalDateTime.now()).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findByItem_ownerAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findByItem_ownerAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findByItem_ownerAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findByItem_ownerAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                default:
                    throw new UnsupportedStateException(String.format("Unknown state: %s", state));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(state);
        }
    }

    @Override
    public BookingDto decideBooking(Long bookingId, Long userId, Boolean approved) {
        final Booking booking = bookingRepository.findById(bookingId).get();
        final Item item = booking.getItem();
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException(String.format("Booking уже подтвержден, bookingId: %d", bookingId));
        }
        BookingStatus status = BookingStatus.REJECTED;
        if (approved) {
            status = BookingStatus.APPROVED;
        }
        final Long ownerId = booking.getItem().getOwner();
        if (ownerId.equals(userId)) {
            booking.setStatus(status);
        } else {
            throw new MismatchUserIdException(String.format(
                    "UserId не является владельцем вещи, ownerId: %d, userId: %d", ownerId, userId));
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    private Boolean dateValidation(BookingCreateRequestDto bookingReq) {
        return (bookingReq.getStart().isAfter(LocalDateTime.now())
                || bookingReq.getStart().equals(LocalDateTime.now()))
                && bookingReq.getEnd().isAfter(LocalDateTime.now())
                && bookingReq.getStart().isBefore(bookingReq.getEnd());
    }
}
