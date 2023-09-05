package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ForUpdateItemDto forUpdateItemDto, Long itemId, Long userId);

    ItemDtoResponse getById(Long itemId, Long userId);

    List<ItemDtoResponse> getAll(Long userId, Integer from, Integer size);

    void deleteById(Long itemId, Long userId);

    List<ItemDto> getItemsByQuery(String query, Integer from, Integer size);

    CommentResponseDto createComment(CommentRequestDto commentReq, Long itemId, Long userId);
}
