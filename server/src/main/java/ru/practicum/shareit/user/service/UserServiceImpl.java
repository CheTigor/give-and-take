package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(User user) {
        final User savedUser = userRepository.save(user);
        log.debug("Успешно создан user: {}", user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto update(ForUpdateUserDto forUpdateUserDto, Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User с id: %d не найден", userId)));
        if (forUpdateUserDto.getName() != null) {
            user.setName(forUpdateUserDto.getName());
        }
        if (forUpdateUserDto.getEmail() != null) {
            user.setEmail(forUpdateUserDto.getEmail());
        }
        log.debug("Успешное обновление user: {}", user);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User с id: %d не найден", userId))));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
        log.debug("Успешное удаление user по id: {}", userId);
    }
}
