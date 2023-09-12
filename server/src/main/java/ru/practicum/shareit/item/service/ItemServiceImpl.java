package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDtoResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(CommentRepository commentRepository, ItemRepository itemRepository,
                           BookingRepository bookingRepository, UserRepository userRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format(
                "Данный пользователь не найден в базе, userId: %d", userId)));
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new
                    ItemRequestNotFoundException(String.format("Данный request не найден в базе, id: %d",
                    itemDto.getRequestId())));
        }
        final Item item = itemRepository.save(ItemMapper.toItem(null, user, itemDto, itemRequest));
        log.debug("Успешное создание item: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ForUpdateItemDto forUpdateItemDto, Long itemId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Данный пользователь не найден в базе, userId: %d", userId));
        }
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Item с id: %d не найден", itemId)));
        userValidate(item, userId);
        if (forUpdateItemDto.getName() != null) {
            item.setName(forUpdateItemDto.getName());
        }
        if (forUpdateItemDto.getDescription() != null) {
            item.setDescription(forUpdateItemDto.getDescription());
        }
        if (forUpdateItemDto.getAvailable() != null) {
            item.setAvailable(forUpdateItemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(item);
        log.debug("Успешное обновление item: {}", item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDtoResponse getById(Long itemId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Данный пользователь не найден в базе, userId: %d", userId));
        }
        if (itemRepository.existsById(itemId)) {
            return itemResponseBuild(itemId, userId);
        } else {
            throw new ItemNotFoundException(String.format("Item с itemId: %d не существует", itemId));
        }
    }

    @Override
    public List<ItemDtoResponse> getAll(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Данный пользователь не найден в базе, userId: %d", userId));
        }
        List<Item> items = itemRepository.findByOwner_idOrderByIdAsc(userId, PageRequest.of(from / size, size)).toList();
        List<ItemDtoResponse> itemsWithDates = new ArrayList<>();
        for (Item item : items) {
            itemsWithDates.add(itemResponseBuild(item.getId(), userId));
        }
        return itemsWithDates;
    }

    @Override
    public void deleteById(Long itemId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Данный пользователь не найден в базе, userId: %d", userId));
        }
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Item с id: %d не найден", itemId)));
        userValidate(item, userId);
        itemRepository.deleteById(itemId);
        log.debug("Успешное удаление item: {}", item);
    }

    @Override
    public List<ItemDto> getItemsByQuery(String query, Integer from, Integer size) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }
        String subString = query.toLowerCase();
        return itemRepository.findByQueryIgnoreCase(subString, PageRequest.of(
                from / size, size)).toList().stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentReq, Long itemId, Long userId) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Item с id: %d не найден", itemId)));
        final User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User с id: %d не найден", userId)));
        final Booking itemBooking = bookingRepository.findFirstByItem_idAndBooker_idAndStatusAndStartIsBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (itemBooking == null) {
            throw new BadRequestException(String.format("Невозможно создать комментарий - нет бронирований данного " +
                    "предмета пользователем, itemId: %d, userId: %d", itemId, userId));
        }
        final Comment comment = commentRepository.save(CommentMapper.toComment(null, commentReq, item, user,
                LocalDateTime.now()));
        log.debug("Успешное создание comment: {}", comment);
        return CommentMapper.toCommentResponse(comment);
    }

    private void userValidate(Item item, long userId) {
        if (item.getOwner().getId() != userId) {
            throw new MismatchUserIdException(String.format("User: %d не является владельцем Item: %d", userId,
                    item.getId()));
        }
    }


    private Booking nextBookingsCalculate(Long itemId, Long userId) {
        final List<Booking> bookingsForNext = bookingRepository.findByItem_idAndItem_owner_idAndStartIsAfterAndStatus(itemId, userId,
                LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookingsForNext.size() != 0) {
            Booking nextBooking = bookingsForNext.get(0);
            for (int i = 1; i < bookingsForNext.size(); i++) {
                if (bookingsForNext.get(i).getStart().isBefore(nextBooking.getStart())) {
                    nextBooking = bookingsForNext.get(i);
                }
            }
            return nextBooking;
        } else {
            return null;
        }
    }

    private Booking lastBookingsCalculate(Long itemId, Long userId) {
        final List<Booking> bookingsForLast = bookingRepository.findByItem_idAndItem_owner_idAndStatusAndStartIsBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (bookingsForLast.size() != 0) {
            Booking lastBooking = bookingsForLast.get(0);
            for (int i = 1; i < bookingsForLast.size(); i++) {
                if (bookingsForLast.get(i).getStart().isAfter(lastBooking.getStart())) {
                    lastBooking = bookingsForLast.get(i);
                }
            }
            return lastBooking;
        } else {
            return null;
        }
    }

    private ItemDtoResponse itemResponseBuild(Long itemId, Long userId) {
        final Booking lastBooking = lastBookingsCalculate(itemId, userId);
        final Booking nextBooking = nextBookingsCalculate(itemId, userId);
        final ItemDtoResponse item = ItemMapper.toItemResponse(itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item с id: %d не найден", itemId))));
        if (lastBooking != null) {
            item.setLastBooking(new BookingForItemDtoResponse(lastBooking.getId(), lastBooking.getBooker().getId()));
        } else if (nextBooking != null) {
            item.setLastBooking(new BookingForItemDtoResponse(nextBooking.getId(), nextBooking.getBooker().getId()));
        }
        if (nextBooking != null) {
            item.setNextBooking(new BookingForItemDtoResponse(nextBooking.getId(), nextBooking.getBooker().getId()));
        } else {
            item.setNextBooking(null);
        }
        item.setComments(commentRepository.findByItemId(itemId).stream().map(CommentMapper::toCommentResponse)
                .collect(Collectors.toList()));
        return item;
    }
}
