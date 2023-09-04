package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.MismatchUserIdException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @Mock
    ItemRepository itemRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    User user;
    ItemDto itemDto;
    Item item;
    ForUpdateItemDto forUpdateItemDto;
    Item updatedItem;
    ItemDtoResponse itemDtoResponse;
    CommentRequestDto commentReq;
    Booking booking;
    Comment comment;

    ItemService itemService;

    @BeforeEach
    void setUp() {
        user = new User(1L, "name", "email@mail.ru");
        itemDto = new ItemDto(1L, "name", "description", true, null);
        item = new Item(1L, "name", "description", true, user, null);
        forUpdateItemDto = new ForUpdateItemDto("updateName", "updateDescription", true);
        updatedItem = new Item(1L, "updateName", "updateDescription", true, user,
                null);
        itemDtoResponse = new ItemDtoResponse(1L, "name", "description", true, null,
                null, List.of());
        commentReq = new CommentRequestDto("text");
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user,
                BookingStatus.APPROVED);
        comment = new Comment(1L, "text", item, user,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        itemService = new ItemServiceImpl(commentRepository, itemRepository, bookingRepository, userRepository,
                itemRequestRepository);
    }

    @Test
    void createItem() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto2 = itemService.create(itemDto, 1L);

        Assertions.assertEquals(itemDto, itemDto2);
    }

    @Test
    void createItemWithUserNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class, () ->
                itemService.create(itemDto, 1L));
    }

    @Test
    void updateItem() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any())).thenReturn(updatedItem);

        ItemDto itemDto1 = itemService.update(forUpdateItemDto, 1L, 1L);

        Assertions.assertEquals(ItemMapper.toItemDto(updatedItem), itemDto1);
    }

    @Test
    void updateItemWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () ->
                itemService.update(forUpdateItemDto, 1L, 1L));
    }

    @Test
    void updateItemWithItemNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemService.update(forUpdateItemDto, 1L, 1L));
    }

    @Test
    void getById() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(anyLong())).thenReturn(true);
        //itemResponseBuild
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItemId(anyLong())).thenReturn(List.of());
        Mockito.when(bookingRepository.findByItem_idAndItem_owner_idAndStatusAndStartIsBefore(any(), any(), any(), any()))
                .thenReturn(List.of());
        Mockito.when(bookingRepository.findByItem_idAndItem_owner_idAndStartIsAfterAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of());

        ItemDtoResponse itemDtoResponse1 = itemService.getById(1L, 1L);

        Assertions.assertEquals(itemDtoResponse, itemDtoResponse1);
    }

    @Test
    void getByIdWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () ->
                itemService.getById(1L, 1L));
    }

    @Test
    void getByIdWithItemNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemService.getById(1L, 1L));
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findByOwner_id(anyLong(), any())).thenReturn(new PageImpl<>(List.of(item)));
        //itemResponseBuild
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItemId(anyLong())).thenReturn(List.of());
        Mockito.when(bookingRepository.findByItem_idAndItem_owner_idAndStatusAndStartIsBefore(any(), any(), any(), any()))
                .thenReturn(List.of());
        Mockito.when(bookingRepository.findByItem_idAndItem_owner_idAndStartIsAfterAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of());

        List<ItemDtoResponse> items = itemService.getAll(1L, 0, 20);

        Assertions.assertEquals(List.of(itemDtoResponse), items);
    }

    @Test
    void getAllWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () ->
                itemService.getAll(1L, 0, 20));
    }

    @Test
    void deleteUserByIdWithUserNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () ->
                itemService.deleteById(1L, 1L));
    }

    @Test
    void deleteUserByIdWithItemNotFoundException() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemService.deleteById(1L, 1L));
    }

    @Test
    void userValidateWithDifferentIds() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Assertions.assertThrows(MismatchUserIdException.class, () ->
                itemService.deleteById(1L, 2L));

    }

    @Test
    void getItemsByQuery() {
        Mockito.when(itemRepository.findByQueryIgnoreCase(anyString(),
                any())).thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> items = itemService.getItemsByQuery("name", 0, 20);

        Assertions.assertEquals(List.of(itemDto), items);
    }

    @Test
    void getItemsByQueryWithBlankQuery() {
        List<ItemDto> items = itemService.getItemsByQuery("", 0, 20);

        Assertions.assertEquals(List.of(), items);
    }

    @Test
    void createComment() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findFirstByItem_idAndBooker_idAndStatusAndStartIsBefore(anyLong(), anyLong(),
                any(), any())).thenReturn(booking);
        Mockito.when(commentRepository.save(any())).thenReturn(comment);

        CommentResponseDto comment1 = itemService.createComment(commentReq, 1L, 1L);

        Assertions.assertEquals(CommentMapper.toCommentResponse(comment), comment1);
    }

    @Test
    void createCommentWithItemNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemService.createComment(commentReq, 1L, 1L));
    }

    @Test
    void createCommentWithUserNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () ->
                itemService.createComment(commentReq, 1L, 1L));
    }

    @Test
    void createCommentWithBadRequestException() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findFirstByItem_idAndBooker_idAndStatusAndStartIsBefore(anyLong(), anyLong(),
                any(), any())).thenReturn(null);

        Assertions.assertThrows(BadRequestException.class, () ->
                itemService.createComment(commentReq, 1L, 1L));
    }
}
