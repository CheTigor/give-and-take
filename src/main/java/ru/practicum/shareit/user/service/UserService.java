package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(ForUpdateUserDto forUpdateUserDto, long id);

    UserDto getById(long id);

    List<UserDto> getAll();

    void deleteById(long id);
}
