package ru.practicum.item.mapper;


import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.CommentDtoOutput;
import ru.practicum.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getItem(),
                commentDto.getAuthor(),
                commentDto.getCreated()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor(),
                comment.getCreated()
        );
    }

    public static List<CommentDtoOutput> toCommentDtosList(List<Comment> comments) {
        List<CommentDtoOutput> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(new CommentDtoOutput(
                    comment.getId(),
                    comment.getText(),
                    comment.getAuthor().getName(),
                    comment.getCreated()
            ));
        }
        return result;
    }

    public static CommentDtoOutput commentToOutput(Comment comment) {
        return new CommentDtoOutput(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
