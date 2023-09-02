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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    BookingDto bookingDto;
    Item item;
    User user;
    BookingCreateRequestDto bookingCRD;

    @BeforeEach
    void setUp() {
        item = new Item(1L, "name", "description", true, 1L, null);
        user = new User(1L, "name", "email@mail.ru");
        bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                LocalDateTime.of(2023, 1, 2, 1, 1, 1), item, user,
                BookingStatus.WAITING);
        bookingCRD = new BookingCreateRequestDto(LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                LocalDateTime.of(2023, 1, 2, 1, 1, 1), 1L);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.create(any(), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCRD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.owner", is(bookingDto.getItem().getOwner()), Long.class))
                .andExpect(jsonPath("$.item.requestId", is(bookingDto.getItem().getRequestId())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void createBookingIn1Line() throws Exception {
        when(bookingService.create(any(), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCRD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void createBookingWithUserNotFoundException() throws Exception {
        when(bookingService.create(any(), anyLong())).thenThrow(UserNotFoundException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCRD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void createBookingWithItemNotFoundException() throws Exception {
        when(bookingService.create(any(), anyLong())).thenThrow(ItemNotFoundException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCRD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void createBookingWithDataValidationException() throws Exception {
        when(bookingService.create(any(), anyLong())).thenThrow(DateValidationException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCRD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void decideBooking() throws Exception {
        when(bookingService.decideBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void decideBookingWithBookingNotFoundException() throws Exception {
        when(bookingService.decideBooking(anyLong(), anyLong(), anyBoolean())).thenThrow(BookingNotFoundException.class);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void decideBookingWithBadRequestException() throws Exception {
        when(bookingService.decideBooking(anyLong(), anyLong(), anyBoolean())).thenThrow(BadRequestException.class);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void decideBookingWithMismatchUserIdException() throws Exception {
        when(bookingService.decideBooking(anyLong(), anyLong(), anyBoolean())).thenThrow(MismatchUserIdException.class);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void decideBookingWith() throws Exception {
        when(bookingService.decideBooking(anyLong(), anyLong(), anyBoolean())).thenThrow(BookingNotFoundException.class);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getBookingByIdWithBookingNotFoundException() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenThrow(BookingNotFoundException.class);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getBookingByIdWithUserNotFoundException() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenThrow(UserNotFoundException.class);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getAllBookings() throws Exception {
        when(bookingService.getAll(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllBookingsWithUserNotFoundException() throws Exception {
        when(bookingService.getAll(anyString(), anyLong(), anyInt(), anyInt())).thenThrow(UserNotFoundException.class);

        mvc.perform(get("/bookings", 1)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getAllBookingsWithIllegalArgumentException() throws Exception {
        when(bookingService.getAll(anyString(), anyLong(), anyInt(), anyInt())).thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/bookings", 1)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }

    @Test
    void getAllOwnerBookings() throws Exception {
        when(bookingService.getOwnerAll(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllOwnerBookingsWithUserNotFoundException() throws Exception {
        when(bookingService.getOwnerAll(anyString(), anyLong(), anyInt(), anyInt())).thenThrow(UserNotFoundException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getAllOwnerBookingsWithIllegalArgumentException() throws Exception {
        when(bookingService.getOwnerAll(anyString(), anyLong(), anyInt(), anyInt())).thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }

}
