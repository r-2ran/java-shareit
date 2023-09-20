package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository requestRepository;


    private User owner;
    private User user;
    private Item item;
    private ItemRequest request;


    @BeforeEach
    void create() {
        owner = userRepository.save(new User(1L, "owner", "owner@mail"));
        user = userRepository.save(new User(2L, "user", "user@mail"));
        request = requestRepository.save(new ItemRequest(
                        1L,
                        "description",
                        user,
                        LocalDateTime.now()
                )
        );
        item = itemRepository.save(new Item(
                1L,
                "item",
                "description",
                true,
                owner,
                request));


    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void searchTest() {
        List<Item> result = itemRepository.search("description");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item, result.get(0));
    }
}
