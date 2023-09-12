package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotAvailableException extends RuntimeException {

    public NotAvailableException(String s) {
        super(s);
        log.warn(s);
    }
}
