package ru.practicum.shareit.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingForItemDtoResponse;

public class BookingModelTest {

    @Test
    void test() {
        BookingForItemDtoResponse bookingForItemDtoResponse = new BookingForItemDtoResponse();
        bookingForItemDtoResponse.setId(1L);
        bookingForItemDtoResponse.setBookerId(1L);
        //Booking booking;

        Assertions.assertEquals(new BookingForItemDtoResponse(1L, 1L), bookingForItemDtoResponse);

    }

    /*public static BookingForItemDtoResponse toBookingForItemDtoResponse(Booking booking) {
        return new BookingForItemDtoResponse(booking.getId(), booking.getBooker().getId());
    }

    public static ru.practicum.shareit.booking.model.Booking toBooking(BookingDto bookingDto) {
        return new ru.practicum.shareit.booking.model.Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), bookingDto.getItem(),
                bookingDto.getBooker(), bookingDto.getStatus());
    }*/
}
