package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestGetResponse;
import ru.practicum.shareit.request.dto.ItemRequestPostRequest;
import ru.practicum.shareit.request.dto.ItemRequestPostResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestPostResponse create(ItemRequestPostRequest description, Long userId);

    List<ItemRequestGetResponse> getByUserId(Long userId);

    List<ItemRequestGetResponse> getAllAnotherRequests(Long requesterId, Integer from, Integer size);

    ItemRequestGetResponse getByRequestId(Long userId, Long requestId);
}
