package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingCreateRequestDto booking, Long bookerId);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAll(String status, Long userId);

    List<BookingDto> getOwnerAll(String state, Long userId);

    BookingDto decideBooking(Long bookingId, Long userId, Boolean approved);
}
