package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Item booker;
}
