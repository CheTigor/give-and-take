package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MismatchUserIdException extends IllegalArgumentException {

    public MismatchUserIdException(String s) {
        super(s);
        log.warn(s);
    }
}
