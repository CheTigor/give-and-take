package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestGetResponse;
import ru.practicum.shareit.request.dto.ItemRequestPostResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    /*public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequester().getId());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user, List<Item> items) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), itemRequestDto.getC, user, items);
    }*/
    /*public static ItemRequest toItemRequest(ItemRequestPostRequest description, Long userId) {
        return new ItemRequest(null, )
    }*/

    public static ItemRequestPostResponse toItemRequestPostResponse(ItemRequest itemRequest) {
        return new ItemRequestPostResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static ItemRequestGetResponse toItemRequestGetResponse(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestGetResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(),
                items);
    }
}
