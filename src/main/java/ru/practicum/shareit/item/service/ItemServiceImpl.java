package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) throws NoSuchUserFound {
        if (userService.userHashMap().containsKey(userId)) {
            return ItemMapper.toItemDto(itemStorage.addItem(userId, itemDto));
        } else {
            throw new NoSuchUserFound(String.format("cannot add item with id = %d, " +
                    "due to no such owner id = %d", itemDto.getId(), userId));
        }
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, int userId)
            throws NoSuchUserFound {
        if (getItemById(itemId, userId).getOwnerId() != userId) {
            throw new NoSuchUserFound(String.format("no such user id = %d", userId));
        } else {
            return ItemMapper.toItemDto(itemStorage.updateItem(itemId, itemDto, userId));

        }
    }

    @Override
    public ItemDto getItemById(int itemId, int userId) {
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId, userId));
    }

    @Override
    public List<ItemDto> searchItem(String text, int userId) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.searchItem(text, userId);
    }

    @Override
    public List<ItemDto> getAllItemsByUser(int userId) {
        return itemStorage.getAllItemsByUser(userId);
    }
}
