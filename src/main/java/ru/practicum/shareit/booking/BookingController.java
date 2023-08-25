package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
                             @RequestHeader("X-Sharer-User-Id") @NotNull Long bookerId) {
        log.info("POST запрос booking create - booking: \n{},\n bookerId: \n{}", bookingReq, bookerId);
        final BookingDto bookingResp = bookingService.create(bookingReq, bookerId);
        log.info("Ответ на POST booking create - booking: \n{}", bookingResp);
        return bookingResp;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto decideBooking(@PathVariable @NotNull Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                    @RequestParam("approved") @NotNull Boolean approved) {
        log.info("PATCH запрос booking decide - bookingId: \n{},\n userId: \n{},\n approved: \n{}", bookingId,
                userId, approved);
        final BookingDto bookingResp = bookingService.decideBooking(bookingId, userId, approved);
        log.info("PATCH ответ booking decide - booking: \n{}", bookingResp);
        return bookingResp;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable @NotNull Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("GET запрос booking getById - bookingId: \n{},\n userId: \n{}", bookingId,
                userId);
        final BookingDto bookingResp = bookingService.getById(bookingId, userId);
        log.info("GET ответ booking getById - booking: \n{}", bookingResp);
        return bookingResp;
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestParam(required = false, defaultValue = "ALL") String state,
                                   @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("GET запрос booking getAll - state: \n{},\n userId: \n{}", state, userId);
        final List<BookingDto> bookingResp = bookingService.getAll(state, userId);
        log.info("GET ответ booking getAll - bookings: \n{}", bookingResp);
        return bookingResp;
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAll(@RequestParam(required = false, defaultValue = "ALL") String state,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("GET запрос booking getOwnerAll - state: \n{},\n iserId: \n{}", state, userId);
        final List<BookingDto> bookingResp = bookingService.getOwnerAll(state, userId);
        log.info("GET ответ booking getOwnerAll - bookings: \n{}", bookingResp);
        return bookingResp;
    }
}
