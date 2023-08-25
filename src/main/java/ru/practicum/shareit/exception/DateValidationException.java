package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateValidationException extends RuntimeException {

    public DateValidationException(String s) {
        super(s);
        log.warn(s);
    }
}
