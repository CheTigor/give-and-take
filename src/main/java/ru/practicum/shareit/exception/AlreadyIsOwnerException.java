package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlreadyIsOwnerException extends RuntimeException {

    public AlreadyIsOwnerException(String s) {
        super(s);
        log.warn(s);
    }
}
