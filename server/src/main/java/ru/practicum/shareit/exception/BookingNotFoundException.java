package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String s) {
        super(s);
        log.warn(s);
    }
}
