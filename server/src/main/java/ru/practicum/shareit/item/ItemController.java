package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST запрос item create - item: \n{},\n userId: \n{}", itemDto, userId);
        final ItemDto itemResp = itemService.create(itemDto, userId);
        log.info("POST ответ item create - item: \n{}", itemResp);
        return itemResp;
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto update(@RequestBody ForUpdateItemDto forUpdateItemDto, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH запрос item update - itemId: \n{},\n userId: \n{}", itemId, userId);
        final ItemDto itemResp = itemService.update(forUpdateItemDto, itemId, userId);
        log.info("PATCH ответ item update - item: \n{}", itemResp);
        return itemResp;
    }

    @GetMapping(value = "/{itemId}")
    public ItemDtoResponse getById(@PathVariable("itemId") Long itemId,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET запрос item getById - itemId: \n{},\n userId: \n{}", itemId, userId);
        final ItemDtoResponse itemResp = itemService.getById(itemId, userId);
        log.info("GET ответ item getById - item: \n{}", itemResp);
        return itemResp;
    }

    @GetMapping
    public List<ItemDtoResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(value = "from") Integer from,
                                        @RequestParam(value = "size") Integer size) {
        log.info("GET запрос item getAll - userId: \n{}", userId);
        final List<ItemDtoResponse> itemResp = itemService.getAll(userId, from, size);
        log.info("GET запрос item getAll - item: \n{}", itemResp);
        return itemResp;
    }

    @DeleteMapping(value = "/{itemId}")
    public void deleteById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("DELETE запрос item deleteById - itemId: \n{},\n userId: \n{}", itemId, userId);
        itemService.deleteById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByQuery(@RequestParam("text") String query,
                                         @RequestParam(value = "from") Integer from,
                                         @RequestParam(value = "size") Integer size) {
        log.info("GET запрос item getItemsByQuery - query: \n{}", query);
        final List<ItemDto> itemResp = itemService.getItemsByQuery(query, from, size);
        log.info("GET ответ item getItemsByQuery - item: \n{}", itemResp);
        return itemResp;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentReq,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable("itemId") Long itemId) {
        log.info("POST запрос item createComment - comment: \n{},\n userId: \n{},\n itemId: \n{}", commentReq, userId,
                itemId);
        final CommentResponseDto commentResp = itemService.createComment(commentReq, itemId, userId);
        log.info("POST запрос item createComment - comment: \n{}", commentResp);
        return commentResp;
    }
}
