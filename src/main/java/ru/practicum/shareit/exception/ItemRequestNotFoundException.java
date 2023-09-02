package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemRequestNotFoundException extends RuntimeException {

    public ItemRequestNotFoundException(String s) {
        super(s);
        log.warn(s);
    }
}
