package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static Item toItem(Long id, User user, ItemDto itemDto, ItemRequest itemRequest) {
        return new Item(id, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), user, itemRequest);
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
