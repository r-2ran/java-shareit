package ru.practicum.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.CommentDtoOutput;
import ru.practicum.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> searchItem(String text, Long userId);

    List<ItemDto> getAllItemsByUser(Long userId);

    CommentDtoOutput addComment(Long itemId, CommentDto commentDto, Long userId);
}
