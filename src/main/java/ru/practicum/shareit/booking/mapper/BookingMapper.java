package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDtoResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), ItemMapper.toItemDto(
                booking.getItem()), booking.getBooker(), booking.getStatus());
    }

    public static Booking toBooking(BookingCreateRequestDto bookingCreateRequestDto, User booker, Item item) {
        return new Booking(null, bookingCreateRequestDto.getStart(), bookingCreateRequestDto.getEnd(),
                item, booker, BookingStatus.WAITING);
    }

    public static BookingForItemDtoResponse toBookingForItemDtoResponse(BookingDto bookingDto) {
        return new BookingForItemDtoResponse(bookingDto.getId(), bookingDto.getBooker().getId());
    }
}
