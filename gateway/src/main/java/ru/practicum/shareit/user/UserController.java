package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserRequestDto user) {
        log.info("POST запрос user create - user: \n{}", user);
        final ResponseEntity<Object> userResp = userClient.createUser(user);
        log.info("POST ответ user create - response: \n{}", userResp);
        return userResp;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserUpdateRequestDto user,
                                             @PathVariable("userId") @Min(1) Long userId) {
        log.info("PATCH запрос user update - user: \n{},\n userId, \n{}", user, userId);
        if (user.getName() != null) {
            if (user.getName().isBlank()) {
                throw new IllegalArgumentException(String.format("Неверный формат имени: %s", user.getName()));
            }
        }
            if (user.getEmail() != null && !EmailValidator.getInstance().isValid(user.getEmail())) {
                throw new IllegalArgumentException(String.format("Неверный формат email: %s", user.getEmail()));
            }
               final ResponseEntity<Object> userResp = userClient.updateUser(user, userId);
            log.info("PATCH ответ user update - response: \n{}", userResp);
            return userResp;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") @Min(1) Long userId) {
        log.info("GET запрос item getById - userId: \n{}", userId);
        final ResponseEntity<Object> userResp = userClient.getUserById(userId);
        log.info("GET запрос item getById - response: \n{}", userResp);
        return userResp;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET запрос item getAll");
        final ResponseEntity<Object> userResp = userClient.getAllUsers();
        log.info("GET ответ item getAll - response: \n{}", userResp);
        return userResp;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("userId") @Min(1) Long userId) {
        log.info("DELETE запрос user deleteById - userId: \n{}", userId);
        final ResponseEntity<Object> userResp = userClient.deleteUserById(userId);
        log.info("DELETE ответ user deleteById - response: \n{}", userId);
        return userResp;
    }
}
