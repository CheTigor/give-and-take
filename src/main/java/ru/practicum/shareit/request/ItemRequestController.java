package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestGetResponse;
import ru.practicum.shareit.request.dto.ItemRequestPostRequest;
import ru.practicum.shareit.request.dto.ItemRequestPostResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestPostResponse create(@RequestBody @Valid ItemRequestPostRequest description,
                                          @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("POST запрос itemRequest create - request: \n{},\n userId: \n{}", description, userId);
        final ItemRequestPostResponse response = itemRequestService.create(description, userId);
        log.info("POST ответ itemRequest create - response: \n{}", response);
        return response;
    }

    @GetMapping
    public List<ItemRequestGetResponse> getByUserId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("Get запрос itemRequest getByUserId - userId: \n{}", userId);
        final List<ItemRequestGetResponse> response = itemRequestService.getByUserId(userId);
        log.info("Get ответ itemRequest getByUserId - response: \n{}", response);
        return response;
    }

    @GetMapping("/all")
    public List<ItemRequestGetResponse> getAllAnotherRequests(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                              @RequestParam(value = "from", defaultValue = "0")
                                                              @Min(0) Integer from,
                                                              @RequestParam(value = "size", defaultValue = "20")
                                                              @Min(1) Integer size) {
        log.info("Get запрос itemRequest getAllAnotherRequests - userId: \n{}, from: \n{}, size: \n{}", userId, from,
                size);
        final List<ItemRequestGetResponse> response = itemRequestService.getAllAnotherRequests(userId, from, size);
        log.info("Get ответ itemRequest getAllAnotherRequests - response: \n{}", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ItemRequestGetResponse getByRequestId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                 @PathVariable @Min(1) Long requestId) {
        log.info("Get запрос itemRequest getByRequestId - userId: \n{}, requestId: \n{}", userId, requestId);
        final ItemRequestGetResponse response = itemRequestService.getByRequestId(userId, requestId);
        log.info("Get ответ itemRequest getByRequestId - response: \n{}", response);
        return response;
    }

}
