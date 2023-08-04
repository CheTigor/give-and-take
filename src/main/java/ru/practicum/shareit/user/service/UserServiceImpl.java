package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public UserDto create(UserDto userDto) {
        emailValidation(userDto.getEmail());
        final User user = UserMapper.toUser(id++, userDto);
        log.debug("Успешно создан user: {}", user);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(ForUpdateUserDto forUpdateUserDto, long id) {
        if (id <= 0 || !users.containsKey(id)) {
            throw new NullPointerException(String.format("User с id: %d не существует", id));
        }
        final User user = users.get(id);
        if (forUpdateUserDto.getName() != null) {
            user.setName(forUpdateUserDto.getName());
        }
        if (forUpdateUserDto.getEmail() != null) {
            if (!user.getEmail().equals(forUpdateUserDto.getEmail())) {
                emailValidation(forUpdateUserDto.getEmail());
            }
            user.setEmail(forUpdateUserDto.getEmail());
        }
        return UserMapper.toUserDto(users.put(id, user));
    }

    @Override
    public UserDto getById(long id) {
        if (users.containsKey(id)) {
            return UserMapper.toUserDto(users.get(id));
        } else {
            throw new NullPointerException(String.format("User с id: %d не существует", id));
        }
    }

    @Override
    public List<UserDto> getAll() {
        return users.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    private void emailValidation(String email) {
        if (users.values().stream().anyMatch(x -> x.getEmail().equals(email))) {
            throw new EmailAlreadyExistException(String.format("Такой email: %s уже существует", email));
        }
    }
}
