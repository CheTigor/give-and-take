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
import ru.practicum.shareit.booking.dto.BookingForItemDtoResponse;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    ItemDto itemDto;
    ForUpdateItemDto forUpdateItemDto;
    ItemDtoResponse itemDtoResponse;
    BookingForItemDtoResponse lastBooking;
    BookingForItemDtoResponse nextBooking;
    CommentRequestDto commentRequestDto;
    CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "item", "description", true, null);
        forUpdateItemDto = new ForUpdateItemDto("name2", "description2", false);
        lastBooking = new BookingForItemDtoResponse(1L, 2L);
        nextBooking = new BookingForItemDtoResponse(2L, 2L);
        itemDtoResponse = new ItemDtoResponse(2L, "name", "description", true, lastBooking,
                nextBooking, null);
        commentRequestDto = new CommentRequestDto("text");
        commentResponseDto = new CommentResponseDto(1L, "text", "author",
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));
    }

    @Test
    void createItem() throws Exception {
        when(itemService.create(any(), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void createItemWithException() throws Exception {
        when(itemService.create(any(), anyLong())).thenThrow(ItemNotFoundException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(forUpdateItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateItemWithException() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong())).thenThrow(ItemNotFoundException.class);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(forUpdateItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoResponse.getLastBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDtoResponse.getLastBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoResponse.getNextBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDtoResponse.getNextBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.comments", is(itemDtoResponse.getComments())));
    }

    @Test
    void getItemByIdWithException() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenThrow(ItemNotFoundException.class);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getAllItemByUserId() throws Exception {
        when(itemService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoResponse))));
    }

    @Test
    void getAllItemByUserIdWithException() throws Exception {
        when(itemService.getAll(anyLong(), anyInt(), anyInt())).thenThrow(UserNotFoundException.class);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void deleteItemById() throws Exception {
        itemService.deleteById(1L, 1L);
        verify(itemService, times(1)).deleteById(1L, 1L);

        mvc.perform(delete("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItemByIdWithException() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 0))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    void getItemsByQuery() throws Exception {
        when(itemService.getItemsByQuery(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "query"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void getItemsByQueryWithEmptyString() throws Exception {
        when(itemService.getItemsByQuery(anyString(), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(get("/items/search", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));
    }

    @Test
    void createCommentByItem() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentResponseDto)));
    }

    @Test
    void createCommentByItemWithException() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong())).thenThrow(BadRequestException.class);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }
}
