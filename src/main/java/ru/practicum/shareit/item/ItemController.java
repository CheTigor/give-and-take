package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ForUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto create(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto update(@RequestBody @Valid ForUpdateItemDto forUpdateItemDto, @PathVariable long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.update(forUpdateItemDto, itemId, userId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getById(@PathVariable("itemId") long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAll(userId);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        itemService.deleteById(id, userId);
    }

    @GetMapping("/search") //фильмы по популярности
    public List<ItemDto> getItemsByQuery(@RequestParam("text") String query) {
        log.info("вызван метод getItemsByQuery - поиск предметов по названию или описанию" +
                " с query " + query);
        return itemService.getItemsByQuery(query);
    }
}
