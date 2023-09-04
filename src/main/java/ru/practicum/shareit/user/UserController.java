package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid User user) {
        log.info("POST запрос user create - user: \n{}", user);
        final UserDto userResp = userService.create(user);
        log.info("POST ответ user create - user: \n{}", userResp);
        return userResp;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody ForUpdateUserDto forUpdateUserDto,
                          @PathVariable("userId") @Min(1) Long userId) {
        log.info("PATCH запрос user update - user: \n{},\n userId, \n{}", forUpdateUserDto, userId);
        final UserDto userResp = userService.update(forUpdateUserDto, userId);
        log.info("PATCH ответ user update - user: \n{}", userResp);
        return userResp;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") @Min(1) Long userId) {
        log.info("GET запрос item getById - userId: \n{}", userId);
        final UserDto userResp = userService.getById(userId);
        log.info("GET запрос item getById - user: \n{}", userResp);
        return userResp;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET запрос item getAll");
        final List<UserDto> userResp = userService.getAll();
        log.info("GET ответ item getAll - user: \n{}", userResp);
        return userResp;
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable("userId") @Min(1) Long userId) {
        log.info("DELETE запрос user deleteById - userId: \n{}", userId);
        userService.deleteById(userId);
    }
}
