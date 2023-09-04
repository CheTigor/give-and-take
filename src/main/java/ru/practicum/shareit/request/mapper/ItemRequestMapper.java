package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestGetResponse;
import ru.practicum.shareit.request.dto.ItemRequestPostResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestPostResponse toItemRequestPostResponse(ItemRequest itemRequest) {
        return new ItemRequestPostResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static ItemRequestGetResponse toItemRequestGetResponse(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestGetResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(),
                items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
    }
}
