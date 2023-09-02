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

    @Autowired
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestPostResponse create(@RequestBody @Valid ItemRequestPostRequest description,
                                          @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        return itemRequestService.create(description, userId);
    }

    @GetMapping
    public List<ItemRequestGetResponse> getByUserId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        return itemRequestService.getByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestGetResponse> getAllAnotherRequests(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                              @RequestParam(value = "from", defaultValue = "0")
                                                              @Min(0) Integer from,
                                                              @RequestParam(value = "size", defaultValue = "20")
                                                              @Min(1) Integer size) {
        return itemRequestService.getAllAnotherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestGetResponse getByRequestId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                 @PathVariable @Min(1) Long requestId) {
        return itemRequestService.getByRequestId(userId, requestId);
    }

}
