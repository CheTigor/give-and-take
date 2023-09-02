package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestGetResponse;
import ru.practicum.shareit.request.dto.ItemRequestPostRequest;
import ru.practicum.shareit.request.dto.ItemRequestPostResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    ItemRequestPostRequest itemRequestPReq;
    ItemRequestPostResponse itemRequestPRes;
    ItemRequestGetResponse itemRequestGRes;

    @BeforeEach
    void setUp() {
        itemRequestPReq = new ItemRequestPostRequest("description");
        itemRequestPRes = new ItemRequestPostResponse(1L, "description", LocalDateTime.now());
        itemRequestGRes = new ItemRequestGetResponse(1L, "description", LocalDateTime.now(), List.of());
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.create(any(), anyLong())).thenReturn(itemRequestPRes);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestPReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestPRes)));
    }

    @Test
    void getByUserIdItemRequest() throws Exception {
        when(itemRequestService.getByUserId(anyLong())).thenReturn(List.of(itemRequestGRes));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestGRes))));
    }

    @Test
    void getAllAnotherRequests() throws Exception {
        when(itemRequestService.getAllAnotherRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestGRes));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestGRes))));
    }

    @Test
    void getByRequestId() throws Exception {
        when(itemRequestService.getByRequestId(anyLong(), anyLong())).thenReturn(itemRequestGRes);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestGRes)));
    }
}
