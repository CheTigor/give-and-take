package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.ForUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTests {

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private User user;
    private ForUpdateUserDto forUpdateUserDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "name", "email@yandex.com");
        user = new User(null, "name", "email@yandex.com");
        forUpdateUserDto = new ForUpdateUserDto("name2", "null");
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void saveNewUserWithException() throws Exception {
        when(userService.create(any()))
                .thenThrow(UserNotFoundException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(any(ForUpdateUserDto.class), anyLong()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(forUpdateUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateNewUserWithException() throws Exception {
        when(userService.update(any(ForUpdateUserDto.class), anyLong()))
                .thenThrow(UserNotFoundException.class);

        mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(forUpdateUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUserByIdWithException() throws Exception {
        when(userService.getById(anyLong()))
                .thenThrow(UserNotFoundException.class);

        mvc.perform(get("/users/{userId}", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.singletonList(userDto))));
    }

    @Test
    void deleteUserById() throws Exception {
        mvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserByIdWithException() throws Exception {
        userService.deleteById(1L);
        verify(userService, times(1)).deleteById(1L);

        mvc.perform(delete("/users/{userId}", 0))
                .andExpect(status().is(400));

        verifyNoMoreInteractions(userService);
    }
}
