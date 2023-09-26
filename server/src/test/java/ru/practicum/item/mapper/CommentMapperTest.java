package ru.practicum.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.CommentDtoOutput;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.item.mapper.CommentMapper.*;

class CommentMapperTest {
    private Comment comment = new Comment(1L, "text", new Item(), new User(), LocalDateTime.now());
    private List<Comment> comments = List.of(comment);

    @Test
    void toCommentDtoTest() {
        CommentDto res = toCommentDto(comment);

        assertEquals(res.getId(), comment.getId());
        assertEquals(res.getText(), comment.getText());
    }

    @Test
    void toCommentDtosListTest() {
        List<CommentDtoOutput> res = toCommentDtosList(comments);

        assertEquals(res.get(0).getId(), comments.get(0).getId());
        assertEquals(res.get(0).getText(), comments.get(0).getText());
    }
}