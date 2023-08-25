package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ForUpdateItemDto forUpdateItemDto, Long itemId, Long userId);

    ItemDtoResponse getById(Long id, Long userId);

    List<ItemDtoResponse> getAll(Long userId);

    void deleteById(Long id, Long userId);

    List<ItemDto> getItemsByQuery(String query);

    CommentResponseDto createComment(CommentRequestDto commentReq, Long itemId, Long userId);
}
