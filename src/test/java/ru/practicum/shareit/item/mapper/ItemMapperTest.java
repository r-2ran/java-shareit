package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

class ItemMapperTest {
    private User owner = new User(2L, "owner", "owner@mail.ru");
    private Item item = new Item(1L, "item", "description", true,
            owner, new ItemRequest());
    private ItemDto itemDto = new ItemDto(1L, "item", "description", true,
            owner, 1L);

    @Test
    void toItemDtoTest() {
        ItemDto res = toItemDto(item);
        assertEquals(res.getName(), item.getName());
        assertEquals(res.getDescription(), item.getDescription());
        assertEquals(res.getAvailable(), item.getIsAvailable());
    }

    @Test
    void toItemTest() {
        Item res = toItem(itemDto);
        assertEquals(res.getName(), itemDto.getName());
        assertEquals(res.getDescription(), itemDto.getDescription());
        assertEquals(res.getIsAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemDtoListTest() {
        List<ItemDto> res = toItemDtoList(List.of(item));

        assertEquals(res.get(0).getName(), itemDto.getName());
        assertEquals(res.get(0).getDescription(), itemDto.getDescription());
        assertEquals(res.get(0).getAvailable(), itemDto.getAvailable());
    }
}