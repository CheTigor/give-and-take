package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item toItem(Long id, long userId, ItemDto itemDto) {
        return new Item(id, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId, null);
    }

    public static ItemDtoResponse toItemResponse(Item item) {
        ItemDtoResponse itemRes = new ItemDtoResponse();
        itemRes.setId(item.getId());
        itemRes.setName(item.getName());
        itemRes.setDescription(item.getDescription());
        itemRes.setAvailable(item.getAvailable());
        return itemRes;
    }

}
