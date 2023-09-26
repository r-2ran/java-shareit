package ru.practicum.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.model.Item;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.item.mapper.ItemMapper.*;

class ItemMapperTest {
    private User owner = new User(2L, "owner", "owner@mail.ru");
    private Item item = new Item(1L, "item", "description", true,
            owner, new ItemRequest());
    private Item itemNoRequest = new Item(1L, "item", "description", true,
            owner, null);
    private ItemDto itemDto = new ItemDto(1L, "item", "description", true,
            owner, 1L);
    private ItemDto itemDtoSmall = new ItemDto("item", "description", true);

    private ItemDto itemDtoNoRequest = new ItemDto(1L, "item", "description", true,
            owner);

    @Test
    void toItemDtoTest() {
        ItemDto res = toItemDto(item);
        assertEquals(res.getName(), item.getName());
        assertEquals(res.getDescription(), item.getDescription());
        assertEquals(res.getAvailable(), item.getIsAvailable());
    }

    @Test
    void toItemDtoTestNoRequest() {
        ItemDto res = toItemDto(itemNoRequest);
        assertEquals(res.getName(), item.getName());
        assertEquals(res.getDescription(), item.getDescription());
        assertEquals(res.getAvailable(), item.getIsAvailable());
    }

    @Test
    void toItemTest() {
        Item res = toItem(itemDtoSmall);
        assertEquals(res.getName(), itemDto.getName());
        assertEquals(res.getDescription(), itemDto.getDescription());
        assertEquals(res.getIsAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemTestNoRequestTest() {
        Item res = toItem(itemDtoNoRequest);
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

    @Test
    void toItemDtoListTestNoRequest() {
        List<ItemDto> res = toItemDtoList(List.of(itemNoRequest));

        assertEquals(res.get(0).getName(), itemDto.getName());
        assertEquals(res.get(0).getDescription(), itemDto.getDescription());
        assertEquals(res.get(0).getAvailable(), itemDto.getAvailable());
    }
}