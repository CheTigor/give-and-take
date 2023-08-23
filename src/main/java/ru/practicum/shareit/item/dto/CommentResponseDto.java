package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    @NotNull
    private Long id;
    @NotBlank
    private String text;
    @NotNull
    private String authorName;
    @NotNull
    private LocalDateTime created;
}
