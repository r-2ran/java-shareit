package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long itemId, ItemDto itemDto, long userId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> searchItem(String text, long userId);

    List<ItemDto> getAllItemsByUser(long userId);

    CommentDtoOutput addComment(long itemId, CommentDto commentDto, long userId);

    void deleteById(long itemId);
}
