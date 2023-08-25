package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDtoResponse;
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

    @Autowired
    public ItemServiceImpl(CommentRepository commentRepository, ItemRepository itemRepository, BookingRepository bookingRepository,
                           UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Данный пользователь не найден в базе, userId: %d", userId));
        }
        final Item item = ItemMapper.toItem(null, userId, itemDto);
        Long itemId = itemRepository.save(item).getId();
        log.debug("Успешное создание item: {}", item);
        return ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Item с id: %d не найден", itemId))));
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
        itemRepository.save(item);
        log.debug("Успешное обновление item: {}", item);
        return ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Item с id: %d не найден", itemId))));
    }

    @Override
    public ItemDtoResponse getById(Long itemId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Данный пользователь не найден в базе, userId: %d", userId));
        }
        if (itemRepository.existsById(itemId)) {
            return itemResponseBuild(itemId, userId);
        } else {
            throw new NullPointerException(String.format("Item с itemId: %d не существует", itemId));
        }
    }

    @Override
    public List<ItemDtoResponse> getAll(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Данный пользователь не найден в базе, userId: %d", userId));
        }
        List<Item> items = itemRepository.findByOwner(userId);
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
    public List<ItemDto> getItemsByQuery(String query) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }
        String subString = query.toLowerCase();
        return itemRepository.findByQueryIgnoreCase(subString);
    }

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentReq, Long itemId, Long userId) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Item с id: %d не найден", itemId)));
        final User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User с id: %d не найден", userId)));
        List<Booking> itemBookings = bookingRepository.findByItem_idAndBooker_idAndStatusAndStartIsBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (itemBookings.size() == 0) {
            throw new BadRequestException(String.format("Невозможно создать комментарий - нет бронирований данного " +
                    "предмета пользователем, itemId: %d, userId: %d", itemId, userId));
        }
        final Comment comment = CommentMapper.toComment(null, commentReq, item, user, LocalDateTime.now());
        commentRepository.save(comment);
        log.debug("Успешное создание comment: {}", comment);
        return CommentMapper.toCommentResponse(comment);
    }

    private void userValidate(Item item, long userId) {
        if (item.getOwner() != userId) {
            throw new MismatchUserIdException(String.format("User: %d не является владельцем Item: %d", userId,
                    item.getId()));
        }
    }


    private Booking nextBookingsCalculate(Long itemId, Long userId) {
        List<Booking> bookingsForNext = bookingRepository.findByItem_idAndItem_ownerAndStartIsAfterAndStatus(itemId, userId,
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
        List<Booking> bookingsForLast = bookingRepository.findByItem_idAndItem_ownerAndStatusAndStartIsBefore(itemId, userId,
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
        ItemDtoResponse item = ItemMapper.toItemResponse(itemRepository.findById(itemId).orElseThrow(() ->
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
