package ru.practicum.shareit.item.storage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.exception.ValidationException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ItemStorageInMemory implements ItemStorage {
    @Getter
    private final HashMap<Long, Item> items = new HashMap<>();
    private final UserStorage userStorage;
    private int generatedId = 1;

    @Autowired
    public ItemStorageInMemory(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Item addItem(long userId, ItemDto itemDto) throws
            ValidationException {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(generatedId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(long itemId, ItemDto itemDto, long userId) throws NoSuchUserFound {
        if (itemDto.getName() != null) {
            items.get(itemId).setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            items.get(itemId).setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            items.get(itemId).setIsAvailable(itemDto.getAvailable());
        }
        return items.get(itemId);
    }

    @Override
    public void deleteItem(int itemId, int userId) {
        items.remove(itemId);
    }

    @Override
    public Item getItemById(long itemId, long userId) {
        return items.get(itemId);
    }

    @Override
    public List<ItemDto> searchItem(String text, long userId) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()
            )) && item.getIsAvailable()) {
                result.add(ItemMapper.toItemDto(item));
            }
        }
        return result;
    }

    @Override
    public List<ItemDto> getAllItemsByUser(long userId) {
        List<ItemDto> res = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                res.add(ItemMapper.toItemDto(item));
            }
        }
        return res;
    }
}
