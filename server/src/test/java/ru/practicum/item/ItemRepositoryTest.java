package ru.practicum.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private User owner;
    private User user;
    private ItemRequest request;
    private Item item;
    private LocalDateTime date = LocalDateTime.now();

    @BeforeEach
    void createModels() {
        user = new User(null, "user", "user@mail.ru");
        owner = new User(null, "owner", "owner@mail.ru");
        userRepository.save(user);
        userRepository.save(owner);

        request = new ItemRequest(null, "description", user, date);
        requestRepository.save(request);

        item = new Item(null, "item", "description", true,
                owner, request);
        itemRepository.save(item);
    }

    @AfterEach
    void deleteModels() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void search() {
        List<Item> items = itemRepository.search("item");
        assertEquals(item, items.get(0));
        assertEquals(items.size(), 1);
    }

    @Test
    void findAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId());
        assertEquals(item, items.get(0));
        assertEquals(items.size(), 1);
    }

    @Test
    void findAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequestId(request.getId());
        assertEquals(item, items.get(0));
        assertEquals(items.size(), 1);
    }

    @Test
    void findByIdAndRequestId() {
        Item result = itemRepository.findByIdAndRequestId(item.getId(), request.getId());
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getDescription(), result.getDescription());

    }
}