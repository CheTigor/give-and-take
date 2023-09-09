package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingCreateRequestDto bookingReq,
                                                @RequestHeader("X-Sharer-User-Id") @Min(1) Long bookerId) {
        log.info("POST запрос booking create - booking: \n{},\n bookerId: \n{}", bookingReq, bookerId);
        final ResponseEntity<Object> bookingResp = bookingClient.createBooking(bookingReq, bookerId);
        log.info("Ответ на POST booking create - response: \n{}", bookingResp);
        return bookingResp;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> decideBooking(@PathVariable @Min(1) Long bookingId,
                                                @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                @RequestParam("approved") @NotNull Boolean approved) {
        log.info("PATCH запрос booking decide - bookingId: \n{},\n userId: \n{},\n approved: \n{}", bookingId,
                userId, approved);
        final ResponseEntity<Object> bookingResp = bookingClient.decideBooking(bookingId, userId, approved);
        log.info("PATCH ответ booking decide - response: \n{}", bookingResp);
        return bookingResp;
    }

    @ResponseBody
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable @Min(1) Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("GET запрос booking getById - bookingId: \n{},\n userId: \n{}", bookingId,
                userId);
        final ResponseEntity<Object> bookingResp = bookingClient.getBookingById(bookingId, userId);
        log.info("GET ответ booking getById - response: \n{}", bookingResp);
        return bookingResp;
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                 @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size,
                                                 @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("GET запрос booking getAll - state: \n{},\n userId: \n{}", stateParam, userId);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        final ResponseEntity<Object> bookingResp = bookingClient.getAllBookings(state, userId, from, size);
        log.info("GET ответ booking getAll - response: \n{}", bookingResp);
        return bookingResp;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerAllBookings(
            @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size,
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("GET запрос booking getOwnerAll - state: \n{},\n iserId: \n{}", stateParam, userId);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        final ResponseEntity<Object> bookingResp = bookingClient.getOwnerAllBookings(state, userId, from, size);
        log.info("GET ответ booking getOwnerAll - response: \n{}", bookingResp);
        return bookingResp;
    }
}
