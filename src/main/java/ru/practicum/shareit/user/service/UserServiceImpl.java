package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(User user) {
        userRepository.save(user);
        log.debug("Успешно создан user: {}", user);
        return UserMapper.toUserDto(userRepository.findById(user.getId()).get());
    }

    @Override
    public UserDto update(ForUpdateUserDto forUpdateUserDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NullPointerException(String.format("User c userId: %d не существует", userId));
        }
        final User user = userRepository.findById(userId).get();
        if (forUpdateUserDto.getName() != null) {
            user.setName(forUpdateUserDto.getName());
        }
        if (forUpdateUserDto.getEmail() != null) {
            user.setEmail(forUpdateUserDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getById(Long userId) {
        if (userRepository.existsById(userId)) {
            return UserMapper.toUserDto(userRepository.findById(userId).get());
        } else {
            throw new NullPointerException(String.format("User с userId: %d не существует", userId));
        }
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }
}
