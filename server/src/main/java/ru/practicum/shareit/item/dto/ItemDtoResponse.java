package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDtoResponse;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForItemDtoResponse lastBooking;
    private BookingForItemDtoResponse nextBooking;
    private List<CommentResponseDto> comments;
}
