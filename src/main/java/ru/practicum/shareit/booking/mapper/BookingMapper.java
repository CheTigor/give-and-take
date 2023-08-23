package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(),
                booking.getBooker(), booking.getStatus());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), bookingDto.getItem(),
                bookingDto.getBooker(), bookingDto.getStatus());
    }

    public static Booking toBooking(BookingCreateRequestDto bookingCreateRequestDto, User booker, Item item) {
        return new Booking(null, bookingCreateRequestDto.getStart(), bookingCreateRequestDto.getEnd(),
                item, booker, BookingStatus.WAITING);
    }

    public static BookingForItemDtoResponse toBookingForItemDtoResponse(Booking booking) {
        return new BookingForItemDtoResponse(booking.getId(), booking.getBooker().getId());
    }
}
