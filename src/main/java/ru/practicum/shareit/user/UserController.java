package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Valid ForUpdateUserDto forUpdateUserDto, @PathVariable("userId") Long userId) {
        return userService.update(forUpdateUserDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable("userId") Long userId) {
        userService.deleteById(userId);
    }
}
