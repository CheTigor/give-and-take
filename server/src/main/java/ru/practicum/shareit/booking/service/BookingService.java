package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingCreateRequestDto booking, Long bookerId);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAll(String status, Long userId, Integer from, Integer size);

    List<BookingDto> getOwnerAll(String state, Long userId, Integer from, Integer size);

    BookingDto decideBooking(Long bookingId, Long userId, Boolean approved);
}
