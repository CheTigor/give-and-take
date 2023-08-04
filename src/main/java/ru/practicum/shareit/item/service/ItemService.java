package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ForUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ForUpdateItemDto forUpdateItemDto, long itemId, long userId);

    ItemDto getById(long id, long userId);

    List<ItemDto> getAll(long userId);

    void deleteById(long id, long userId);

    List<ItemDto> getItemsByQuery(String query);
}
