package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemRequestDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("POST запрос item create - item: \n{},\n userId: \n{}", itemDto, userId);
        final ResponseEntity<Object> itemResp = itemClient.createItem(itemDto, userId);
        log.info("POST ответ item create - response: \n{}", itemResp);
        return itemResp;
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> updateItemById(@RequestBody @Valid ItemUpdateRequestDto forUpdateItemDto,
                                                 @PathVariable @Min(1) Long itemId,
                                                 @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("PATCH запрос item update - itemId: \n{},\n userId: \n{}", itemId, userId);
        final ResponseEntity<Object> itemResp = itemClient.updateItemById(forUpdateItemDto, itemId, userId);
        log.info("PATCH ответ item update - response: \n{}", itemResp);
        return itemResp;
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") @Min(1) Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("GET запрос item getById - itemId: \n{},\n userId: \n{}", itemId, userId);
        final ResponseEntity<Object> itemResp = itemClient.getItemById(itemId, userId);
        log.info("GET ответ item getById - response: \n{}", itemResp);
        return itemResp;
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                           @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        log.info("GET запрос item getAll - userId: \n{}", userId);
        final ResponseEntity<Object> itemResp = itemClient.getItems(userId, from, size);
        log.info("GET ответ item getAll - response: \n{}", itemResp);
        return itemResp;
    }

    @DeleteMapping(value = "/{itemId}")
    public ResponseEntity<Object> deleteItemById(@PathVariable @Min(1) Long itemId, @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("DELETE запрос item deleteById - itemId: \n{},\n userId: \n{}", itemId, userId);
        final ResponseEntity<Object> itemResp = itemClient.deleteItemById(itemId, userId);
        log.info("DELETE ответ item deleteById - response: \n{}", itemResp);
        return itemResp;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByQuery(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                  @RequestParam("text") @NotNull String query,
                                                  @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        log.info("GET запрос item getItemsByQuery - query: \n{}", query);
        final ResponseEntity<Object> itemResp = itemClient.getItemsByQuery(query, userId, from, size);
        log.info("GET ответ item getItemsByQuery - response: \n{}", itemResp);
        return itemResp;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentRequestDto commentReq,
                                                @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                @PathVariable("itemId") @Min(1) Long itemId) {
        log.info("POST запрос item createComment - comment: \n{},\n userId: \n{},\n itemId: \n{}", commentReq, userId,
                itemId);
        final ResponseEntity<Object> commentResp = itemClient.createComment(commentReq, itemId, userId);
        log.info("POST запрос item createComment - response: \n{}", commentResp);
        return commentResp;
    }
}
