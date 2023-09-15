package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemStorage {
    Item addItem(long userId, ItemDto itemDto);

    Item updateItem(long itemId, ItemDto itemDto, long userId);

    void deleteItem(int itemId, int userId);

    Item getItemById(long itemId, long userId);

    List<ItemDto> searchItem(String text, long userId);

    List<ItemDto> getAllItemsByUser(long userId);

}
