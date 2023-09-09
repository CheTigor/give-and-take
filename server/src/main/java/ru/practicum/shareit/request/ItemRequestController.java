package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestGetResponse;
import ru.practicum.shareit.request.dto.ItemRequestPostRequest;
import ru.practicum.shareit.request.dto.ItemRequestPostResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestPostResponse create(@RequestBody ItemRequestPostRequest description,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST запрос itemRequest create - request: \n{},\n userId: \n{}", description, userId);
        final ItemRequestPostResponse response = itemRequestService.create(description, userId);
        log.info("POST ответ itemRequest create - response: \n{}", response);
        return response;
    }

    @GetMapping
    public List<ItemRequestGetResponse> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get запрос itemRequest getByUserId - userId: \n{}", userId);
        final List<ItemRequestGetResponse> response = itemRequestService.getByUserId(userId);
        log.info("Get ответ itemRequest getByUserId - response: \n{}", response);
        return response;
    }

    @GetMapping("/all")
    public List<ItemRequestGetResponse> getAllAnotherRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(value = "from") Integer from,
                                                              @RequestParam(value = "size") Integer size) {
        log.info("Get запрос itemRequest getAllAnotherRequests - userId: \n{}, from: \n{}, size: \n{}", userId, from,
                size);
        final List<ItemRequestGetResponse> response = itemRequestService.getAllAnotherRequests(userId, from, size);
        log.info("Get ответ itemRequest getAllAnotherRequests - response: \n{}", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ItemRequestGetResponse getByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get запрос itemRequest getByRequestId - userId: \n{}, requestId: \n{}", userId, requestId);
        final ItemRequestGetResponse response = itemRequestService.getByRequestId(userId, requestId);
        log.info("Get ответ itemRequest getByRequestId - response: \n{}", response);
        return response;
    }

}
