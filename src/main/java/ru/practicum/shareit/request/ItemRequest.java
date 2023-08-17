package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {

    private long id;
    @NotBlank
    private String description;
    @NotNull
    private long requester;
    @NotNull
    private LocalDateTime created;

}
