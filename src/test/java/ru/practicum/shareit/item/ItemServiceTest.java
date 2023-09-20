package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;


//    @Test
//    public void addItemTest() {
//        UserDto userDto = userService.addUser(new UserDto(1L, "user", "user@mail.ru"));
//        ItemDto itemDto = itemService.addItem(1L, new ItemDto("item", "description", true));
//        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
//        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();
//
//
//    }

    @Test
    public void getByIdTest() {
        UserDto userDto = userService.addUser(new UserDto(1L, "user", "user@mail.ru"));
        ItemDto itemDto = itemService.addItem(1L, new ItemDto("item", "description", true));
        UserDto savedOwnerDto = userService.addUser(new UserDto("user", "user@email.com"));
        ItemDto savedItemDto = itemService.addItem(savedOwnerDto.getId(), itemDto);
        ItemDto searchedItemDto = itemService.getItemById(savedItemDto.getId(), savedOwnerDto.getId());

        assertNotNull(searchedItemDto);
        assertEquals(savedItemDto.getName(), searchedItemDto.getName());
        assertEquals(savedItemDto.getDescription(), searchedItemDto.getDescription());
        assertEquals(savedItemDto.getAvailable(), searchedItemDto.getAvailable());
    }
}
