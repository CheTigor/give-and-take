package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestPostRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody @Valid ItemRequestPostRequest description,
                                         @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("POST запрос itemRequest create - request: \n{},\n userId: \n{}", description, userId);
        final ResponseEntity<Object> response = itemRequestClient.createRequest(description, userId);
        log.info("POST ответ itemRequest create - response: \n{}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getRequestByUserId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("Get запрос itemRequest getByUserId - userId: \n{}", userId);
        final ResponseEntity<Object> response = itemRequestClient.getRequestByUserId(userId);
        log.info("Get ответ itemRequest getByUserId - response: \n{}", response);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllAnotherRequests(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                        @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                        @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        log.info("Get запрос itemRequest getAllAnotherRequests - userId: \n{}, from: \n{}, size: \n{}", userId, from,
                size);
        final ResponseEntity<Object> response = itemRequestClient.getAllAnotherRequests(userId, from, size);
        log.info("Get ответ itemRequest getAllAnotherRequests - response: \n{}", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                 @PathVariable @Min(1) Long requestId) {
        log.info("Get запрос itemRequest getByRequestId - userId: \n{}, requestId: \n{}", userId, requestId);
        final ResponseEntity<Object> response = itemRequestClient.getRequestById(userId, requestId);
        log.info("Get ответ itemRequest getByRequestId - response: \n{}", response);
        return response;
    }
}
