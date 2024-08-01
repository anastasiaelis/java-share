package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.ItemComment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(ItemComment comment) {
        return new CommentDto(
                comment.getText());
    }

    public CommentDtoOut toCommentDtoOut(ItemComment comment) {
        return new CommentDtoOut(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getItem().getId());
    }

    public ItemComment toComment(CommentDto commentDto, Item item, User user) {
        return new ItemComment(
                commentDto.getText(),
                item,
                user);
    }
}