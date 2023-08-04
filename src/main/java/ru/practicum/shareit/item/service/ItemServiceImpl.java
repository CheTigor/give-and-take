package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.MismatchUserIdException;
import ru.practicum.shareit.item.dto.ForUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final UserService userService;

    @Autowired
    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        final UserDto user = userService.getById(userId);
        final Item item = ItemMapper.toItem(id++, userId, itemDto);
        items.put(item.getId(), item);
        log.debug("Успешное создание item: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ForUpdateItemDto forUpdateItemDto, long itemId, long userId) {
        if (itemId <= 0 || !items.containsKey(itemId)) {
            throw new NullPointerException(String.format("Item с id: %d не существует", itemId));
        }
        final UserDto userDto = userService.getById(userId);
        final Item item = items.get(itemId);
        userValidate(itemId, userId);
        if (forUpdateItemDto.getName() != null) {
            item.setName(forUpdateItemDto.getName());
        }
        if (forUpdateItemDto.getDescription() != null) {
            item.setDescription(forUpdateItemDto.getDescription());
        }
        if (forUpdateItemDto.getAvailable() != null) {
            item.setAvailable(forUpdateItemDto.getAvailable());
        }
        items.put(itemId, item);
        log.debug("Успешное обновление item: {}", item);
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto getById(long id, long userId) {
        if (items.containsKey(id)) {
            return ItemMapper.toItemDto(items.get(id));
        } else {
            throw new NullPointerException(String.format("Item с id: %d не существует", id));
        }
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        return items.values().stream().filter(x -> x.getOwner() == userId).map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long id, long userId) {
        if (items.containsKey(id)) {
            userValidate(id, userId);
            final Item item = items.remove(id);
            log.debug("Успешное удаление item: {}", item);
        }
    }

    @Override
    public List<ItemDto> getItemsByQuery(String query) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }
        String subString = query.toLowerCase();
        List<ItemDto> searchItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable()) {
                if (item.getName().toLowerCase().matches("(.*)" + subString + "(.*)")) {
                    searchItems.add(ItemMapper.toItemDto(item));
                } else if (item.getDescription().toLowerCase().matches("(.*)" + subString + "(.*)")) {
                    searchItems.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return searchItems;
    }

    private void userValidate(long itemId, long userId) {
        final Item item = items.get(itemId);
        if (item.getOwner() != userId) {
                throw new MismatchUserIdException(String.format("User: %d не является владельцем Item: %d", userId, itemId));
        }
    }
}
