package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailAlreadyExistException extends IllegalArgumentException {

    public EmailAlreadyExistException(String s) {
        super(s);
        log.warn(s);
    }
}
