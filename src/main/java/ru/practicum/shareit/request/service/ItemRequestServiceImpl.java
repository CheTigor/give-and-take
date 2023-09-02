package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestGetResponse;
import ru.practicum.shareit.request.dto.ItemRequestPostRequest;
import ru.practicum.shareit.request.dto.ItemRequestPostResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestPostResponse create(ItemRequestPostRequest description, Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User с id: %d не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(null, description.getDescription(),
                LocalDateTime.now(), user));
        log.debug("Успешное сохранение request: {}", itemRequest);
        return ItemRequestMapper.toItemRequestPostResponse(itemRequest);
    }

    @Override
    public List<ItemRequestGetResponse> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("User с id: %d не найден", userId));
        }
        List<ItemRequestGetResponse> response = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequester_idOrderByCreatedDesc(userId);
        for (ItemRequest itemRequest : itemRequests) {
            response.add(getResponseBuild(itemRequest));
        }
        return response;
    }

    @Override
    public List<ItemRequestGetResponse> getAllAnotherRequests(Long requesterId, Integer from, Integer size) {
        if (!userRepository.existsById(requesterId)) {
            throw new UserNotFoundException(String.format("User с id: %d не найден", requesterId));
        }
        List<ItemRequestGetResponse> response = new ArrayList<>();
        List<ItemRequest> allRequests = itemRequestRepository.findNotByRequester_Id(requesterId, PageRequest.of(
                from / size, size));
        for (ItemRequest itemRequest : allRequests) {
            response.add(getResponseBuild(itemRequest));
        }
        return response;
    }

    @Override
    public ItemRequestGetResponse getByRequestId(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("User с id: %d не найден", userId));
        }
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ItemRequestNotFoundException(String.format("Request нет в базе: %d", requestId)));
        return getResponseBuild(request);
    }

    private ItemRequestGetResponse getResponseBuild(ItemRequest itemRequest) {
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
        return ItemRequestMapper.toItemRequestGetResponse(itemRequest, items);
    }
}
