package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public BookingDto create(BookingCreateRequestDto bookingReq, Long bookerId) {
        final User user = userRepository.findById(bookerId).orElseThrow(() -> new UserNotFoundException(
                String.format("User с id: %d не найден", bookerId)));
        final Item item = itemRepository.findById(bookingReq.getItemId()).orElseThrow(() -> new ItemNotFoundException(
                String.format("Item с id: %d не найден", bookingReq.getItemId())));
        if (!dateValidation(bookingReq)) {
            throw new DateValidationException("Неверный формат даты");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new AlreadyIsOwnerException(String.format("Booker уже является владельцем вещи, bookerId: %d, itemId: %d",
                    bookerId, item.getId()));
        }
        if (item.getAvailable().equals(true)) {
            final Booking bookingWithId = bookingRepository.save(BookingMapper.toBooking(bookingReq, user, item));
            log.debug("Успешное создание booking: {}", bookingWithId);
            return BookingMapper.toBookingDto(bookingWithId);
        } else {
            throw new NotAvailableException("Item не доступен для бронирования");
        }
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        final Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(
                String.format("Booking с id: %d не найден", bookingId)));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new UserNotFoundException(String.format(
                    "User не является автором бронирования или владельцем вещи или не существует, userId: %d", userId));
        }
    }

    @Override
    public List<BookingDto> getAll(String state, Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Userid не существует в базе, userId: %d", userId));
        }
        try {
            BookingState stateLC = BookingState.valueOf(state);
            switch (stateLC) {
                case ALL:
                    return bookingRepository.findByBooker_idOrderByStartDesc(userId, PageRequest.of(from / size, size))
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findByBooker_idAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId,
                                    LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from / size, size)).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                                    PageRequest.of(from / size, size)).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findByBooker_idAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                    PageRequest.of(from / size, size)).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId, BookingStatus.WAITING,
                                    PageRequest.of(from / size, size)).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED,
                                    PageRequest.of(from / size, size)).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                default:
                    return List.of();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(state);
        }
    }

    @Override
    public List<BookingDto> getOwnerAll(String state, Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("User с id: %d не найден", userId));
        }
        try {
            BookingState stateLC = BookingState.valueOf(state);
            switch (stateLC) {
                case ALL:
                    return bookingRepository.findByItem_owner_idOrderByStartDesc(userId, PageRequest.of(from / size, size)).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findByItem_owner_idAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId,
                                    LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from / size, size)).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findByItem_owner_idAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                                    PageRequest.of(from / size, size)).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findByItem_owner_idAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                    PageRequest.of(from / size, size)).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findByItem_owner_idAndStatusOrderByStartDesc(userId, BookingStatus.WAITING,
                                    PageRequest.of(from / size, size)).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findByItem_owner_idAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED,
                                    PageRequest.of(from / size, size)).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                default:
                    return List.of();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(state);
        }
    }

    @Transactional
    @Override
    public BookingDto decideBooking(Long bookingId, Long userId, Boolean approved) {
        final Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(
                String.format("Booking с id: %d не найден", bookingId)));
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException(String.format("Booking уже подтвержден, bookingId: %d", bookingId));
        }
        BookingStatus status = BookingStatus.REJECTED;
        if (approved) {
            status = BookingStatus.APPROVED;
        }
        final Long ownerId = booking.getItem().getOwner().getId();
        if (ownerId.equals(userId)) {
            booking.setStatus(status);
        } else {
            throw new MismatchUserIdException(String.format(
                    "UserId не является владельцем вещи, ownerId: %d, userId: %d", ownerId, userId));
        }
        Booking savedBooking = bookingRepository.save(booking);
        log.debug("Успешное сохранение booking: {}", savedBooking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    private Boolean dateValidation(BookingCreateRequestDto bookingReq) {
        return (bookingReq.getStart().isAfter(LocalDateTime.now())
                || bookingReq.getStart().equals(LocalDateTime.now()))
                && bookingReq.getEnd().isAfter(LocalDateTime.now())
                && bookingReq.getStart().isBefore(bookingReq.getEnd());
    }
}
