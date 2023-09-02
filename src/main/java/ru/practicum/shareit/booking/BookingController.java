package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody @Valid BookingCreateRequestDto bookingReq,
                             @RequestHeader("X-Sharer-User-Id") @Min(1) Long bookerId) {
        log.info("POST запрос booking create - booking: \n{},\n bookerId: \n{}", bookingReq, bookerId);
        final BookingDto bookingResp = bookingService.create(bookingReq, bookerId);
        log.info("Ответ на POST booking create - booking: \n{}", bookingResp);
        return bookingResp;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto decideBooking(@PathVariable @Min(1) Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                    @RequestParam("approved") @NotNull Boolean approved) {
        log.info("PATCH запрос booking decide - bookingId: \n{},\n userId: \n{},\n approved: \n{}", bookingId,
                userId, approved);
        final BookingDto bookingResp = bookingService.decideBooking(bookingId, userId, approved);
        log.info("PATCH ответ booking decide - booking: \n{}", bookingResp);
        return bookingResp;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable @Min(1) Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("GET запрос booking getById - bookingId: \n{},\n userId: \n{}", bookingId,
                userId);
        final BookingDto bookingResp = bookingService.getById(bookingId, userId);
        log.info("GET ответ booking getById - booking: \n{}", bookingResp);
        return bookingResp;
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                   @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                   @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size,
                                   @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("GET запрос booking getAll - state: \n{},\n userId: \n{}", state, userId);
        final List<BookingDto> bookingResp = bookingService.getAll(state, userId, from, size);
        log.info("GET ответ booking getAll - bookings: \n{}", bookingResp);
        return bookingResp;
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAll(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                        @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                        @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size,
                                        @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("GET запрос booking getOwnerAll - state: \n{},\n iserId: \n{}", state, userId);
        final List<BookingDto> bookingResp = bookingService.getOwnerAll(state, userId, from, size);
        log.info("GET ответ booking getOwnerAll - bookings: \n{}", bookingResp);
        return bookingResp;
    }
}
