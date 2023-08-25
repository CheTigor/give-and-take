package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    public ItemDto create(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("POST запрос item create - item: \n{},\n userId: \n{}", itemDto, userId);
        final ItemDto itemResp = itemService.create(itemDto, userId);
        log.info("POST ответ item create - item: \n{}", itemResp);
        return itemResp;
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto update(@RequestBody @Valid ForUpdateItemDto forUpdateItemDto, @PathVariable @NotNull Long itemId,
                          @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("PATCH запрос item update - itemId: \n{},\n userId: \n{}", itemId, userId);
        final ItemDto itemResp = itemService.update(forUpdateItemDto, itemId, userId);
        log.info("PATCH ответ item update - item: \n{}", itemResp);
        return itemResp;
    }

    @GetMapping(value = "/{itemId}")
    public ItemDtoResponse getById(@PathVariable("itemId") @NotNull Long itemId,
                                   @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("GET запрос item getById - itemId: \n{},\n userId: \n{}", itemId, userId);
        final ItemDtoResponse itemResp = itemService.getById(itemId, userId);
        log.info("GET ответ item getById - item: \n{}", itemResp);
        return itemResp;
    }

    @GetMapping
    public List<ItemDtoResponse> getAll(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("GET запрос item getAll - userId: \n{}", userId);
        final List<ItemDtoResponse> itemResp = itemService.getAll(userId);
        log.info("GET запрос item getAll - item: \n{}", itemResp);
        return itemResp;
    }

    @DeleteMapping(value = "/{itemId}")
    public void deleteById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("DELETE запрос item deleteById - itemId: \n{},\n userId: \n{}", itemId, userId);
        itemService.deleteById(itemId, userId);
    }

    @GetMapping("/search") //фильмы по популярности
    public List<ItemDto> getItemsByQuery(@RequestParam("text") @NotBlank String query) {
        log.info("GET запрос item getItemsByQuery - query: \n{}", query);
        final List<ItemDto> itemResp = itemService.getItemsByQuery(query);
        log.info("GET ответ item getItemsByQuery - item: \n{}", itemResp);
        return itemResp;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestBody @Valid CommentRequestDto commentReq,
                                            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                            @PathVariable("itemId") @NotNull Long itemId) {
        log.info("POST запрос item createComment - comment: \n{},\n userId: \n{},\n itemId: \n{}", commentReq, userId,
                itemId);
        final CommentResponseDto commentResp = itemService.createComment(commentReq, itemId, userId);
        log.info("POST запрос item createComment - comment: \n{}", commentResp);
        return commentResp;
    }
}
