package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestBody @Valid BookingCreateRequestDto bookingReq,
                             @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.create(bookingReq, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto decideBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam("approved") Boolean approved) {
        return bookingService.decideBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestParam(required = false, defaultValue = "ALL") String state,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getAll(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAll(@RequestParam(required = false, defaultValue = "ALL") String state,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getOwnerAll(state, userId);
    }
}
