package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(
        properties = "db.name=test1",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;

    @Test
    void updateUserTest() {
        UserDto userDto = userService.create(new User(null, "name", "mail@mail.ru"));

        UserDto updatedUser = userService.update(new ForUpdateUserDto("updateName", "update@mail.ru"), 1L);

        UserDto expectedUser = new UserDto(1L, "updateName", "update@mail.ru");

        assertThat(expectedUser, equalTo(updatedUser));
    }
}
