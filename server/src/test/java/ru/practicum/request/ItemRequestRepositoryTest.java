package ru.practicum.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.request.RequestMapper.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private User requestor;
    private User owner;
    private ItemRequest request;
    private Item item;
    private LocalDateTime date = LocalDateTime.now();
    private Pageable pageable = PageRequest.of(0, 5);


    @BeforeEach
    void create() {
        requestor = userRepository.save(new User(null, "user", "user@mail.com"));
        owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        item = itemRepository.save(new Item(null, "item", "description",
                true, owner, null));
        request = requestRepository.save(new ItemRequest(null, "Description", requestor,
                date.minusHours(5)));
    }

    @AfterEach
    void delete() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> requests =
                requestRepository.findAllByRequestorId(requestor.getId(), Sort.unsorted());
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getId(), request.getId());
    }

    @Test
    void findAllByRequestorIdNot() {
        List<ItemRequest> requests =
                requestRepository.findAllByRequestorIdNot(owner.getId(), pageable);
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getId(), request.getId());
    }
}
