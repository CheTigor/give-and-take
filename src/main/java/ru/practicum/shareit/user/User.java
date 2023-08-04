package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class User {

    @NotNull
    private long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
