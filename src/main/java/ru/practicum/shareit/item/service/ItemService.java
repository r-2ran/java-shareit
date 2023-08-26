package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {
    ItemDto addItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int itemId, ItemDto itemDto, int userId);

    ItemDto getItemById(int itemId, int userId);

    List<ItemDto> searchItem(String text, int userId);

    List<ItemDto> getAllItemsByUser(int userId);

}
