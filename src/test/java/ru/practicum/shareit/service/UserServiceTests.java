package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    UserRepository userRepository;

    User userWithId;
    User userWithoutId;
    UserDto userDto;
    UserService userService;
    ForUpdateUserDto forUpdateUserDto;

    @BeforeEach
    void setUp() {
        userWithId = new User(1L, "name", "email@mail.ru");
        userWithoutId = new User(null, "name", "email@mail.ru");
        userDto = new UserDto(1L, "name", "email@mail.ru");
        forUpdateUserDto = new ForUpdateUserDto("name", "email@mail.ru");

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUser() {
        Mockito.when(userRepository.save(any())).thenReturn(userWithId);

        UserDto userDto1 = userService.create(userWithoutId);

        Assertions.assertEquals(userDto, userDto1);
    }

    @Test
    void updateUser() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(userWithId));
        Mockito.when(userRepository.save(any())).thenReturn(userWithId);

        UserDto userDto1 = userService.update(forUpdateUserDto, 1L);

        Assertions.assertEquals(userDto, userDto1);
    }

    @Test
    void updateUserWithUserNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () ->
                userService.update(forUpdateUserDto, 1L));
    }

    @Test
    void getByUserId() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(userWithId));

        UserDto userDto1 = userService.getById(1L);

        Assertions.assertEquals(userDto, userDto1);
    }

    @Test
    void getByUserIdWithUserNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () ->
                userService.getById(1L));
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(userWithId));

        List<UserDto> userDto1 = userService.getAll();

        Assertions.assertEquals(List.of(userDto), userDto1);
    }
}
