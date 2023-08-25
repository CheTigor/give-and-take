package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(Long id, CommentRequestDto commentRequestDto, Item item, User user,
                                    LocalDateTime created) {
        return new Comment(id, commentRequestDto.getText(), item, user, created);
    }

    public static CommentResponseDto toCommentResponse(Comment comment) {
        return new CommentResponseDto(comment.getId(), comment.getText(), comment.getUser().getName(),
                comment.getCreated());
    }
}
