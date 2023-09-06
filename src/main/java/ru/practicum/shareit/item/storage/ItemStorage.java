package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemStorage {
    Item addItem(int userId, ItemDto itemDto);

    Item updateItem(int itemId, ItemDto itemDto, int userId);

    void deleteItem(int itemId, int userId);

    Item getItemById(int itemId, int userId);

    List<ItemDto> searchItem(String text, int userId);

    List<ItemDto> getAllItemsByUser(int userId);

}
