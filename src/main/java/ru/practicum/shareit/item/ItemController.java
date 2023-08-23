package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public ItemDto create(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto update(@RequestBody @Valid ForUpdateItemDto forUpdateItemDto, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(forUpdateItemDto, itemId, userId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDtoResponse getById(@PathVariable("itemId") Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteById(id, userId);
    }

    @GetMapping("/search") //фильмы по популярности
    public List<ItemDto> getItemsByQuery(@RequestParam("text") String query) {
        log.info("вызван метод getItemsByQuery - поиск предметов по названию или описанию" +
                " с query " + query);
        return itemService.getItemsByQuery(query);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentReq, @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable("itemId") Long itemId) {
        return itemService.createComment(commentReq, itemId, userId);
    }
}
