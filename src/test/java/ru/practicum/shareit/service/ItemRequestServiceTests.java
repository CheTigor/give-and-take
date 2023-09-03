package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestGetResponse;
import ru.practicum.shareit.request.dto.ItemRequestPostRequest;
import ru.practicum.shareit.request.dto.ItemRequestPostResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTests {

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemRequest itemRequest;

    private ItemRequestService itemRequestService;
    private ItemRequestPostRequest description;

    @BeforeEach
    void setUp() {
        user = new User(1L, "name", "email@mail.ru");
        itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), user);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        description = new ItemRequestPostRequest("description");
    }

    @Test
    void createRequest() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestPostResponse itemRequestPostResponse = itemRequestService.create(description, user.getId());

        Assertions.assertEquals(itemRequestPostResponse, ItemRequestMapper.toItemRequestPostResponse(itemRequest));
    }

    @Test
    void createRequestWithUserNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> itemRequestService.create(description, user.getId()));
    }

    @Test
    void getAllByUserId() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());
        Mockito.when(itemRequestRepository.findByRequester_idOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestGetResponse> response = itemRequestService.getByUserId(user.getId());

        Assertions.assertEquals(List.of(ItemRequestMapper.toItemRequestGetResponse(itemRequest, List.of())), response);
    }

    @Test
    void getAllByUserIdWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () -> itemRequestService.getByUserId(user.getId()));
    }

    @Test
    void getAllWithoutId() {
        Mockito.when(itemRequestRepository.findNotByRequester_Id(anyLong(), any())).thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);


        List<ItemRequestGetResponse> response = itemRequestService.getAllAnotherRequests(1L, 0, 20);

        Assertions.assertEquals(List.of(ItemRequestMapper.toItemRequestGetResponse(itemRequest, List.of())), response);
    }

    @Test
    void getAllWithoutIdWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () -> itemRequestService.getAllAnotherRequests(1L, 0, 20));
    }

    @Test
    void getByRequestId() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestGetResponse itemRequestGRes = itemRequestService.getByRequestId(1L, 1L);

        Assertions.assertEquals(ItemRequestMapper.toItemRequestGetResponse(itemRequest, List.of()), itemRequestGRes);
    }

    @Test
    void getByRequestIdWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () -> itemRequestService.getByRequestId(1L, 1L));
    }

    @Test
    void getByRequestIdWithItemRequestNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getByRequestId(1L, 1L));
    }
}
