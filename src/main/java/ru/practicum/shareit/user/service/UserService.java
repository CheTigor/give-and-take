package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto create(User user);

    UserDto update(ForUpdateUserDto forUpdateUserDto, Long id);

    UserDto getById(Long id);

    List<UserDto> getAll();

    void deleteById(Long id);
}
