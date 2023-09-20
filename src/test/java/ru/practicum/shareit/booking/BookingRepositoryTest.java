//package ru.practicum.shareit.booking;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.request.repository.ItemRequestRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//public class BookingRepositoryTest {
//    @Autowired
//    private ItemRequestRepository itemRequestRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//    private final LocalDateTime date = LocalDateTime.now();
//    private final User user = userRepository.save(new User(1L, "user", "user@email.com"));
//    private final User requestor = userRepository.save(new User(2L, "requestor", "requestor@email.com"));
//    private final ItemRequest request = itemRequestRepository.save(new ItemRequest(2L, "request", requestor, date));
//
//    @Test
//    public void findAllByRequestorIdNotTest() {
//        Page<ItemRequest> result = itemRequestRepository
//                .findAllByRequestorIdNot(requestor.getId(), Pageable.unpaged());
//
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//        assertEquals(request.getDescription(), result.getContent().get(0).getDescription());
//        assertEquals(request.getRequestor(), result.getContent().get(0).getRequestor());
//        assertEquals(request.getCreated(), result.getContent().get(0).getCreated());
//    }
//
//    @Test
//    public void findAllTest() {
//        List<ItemRequest> result = itemRequestRepository
//                .findAllByRequestorId(user.getId(), Sort.unsorted());
//
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//        assertEquals(request.getDescription(), result.get(0).getDescription());
//        assertEquals(request.getRequestor(), result.get(0).getRequestor());
//        assertEquals(request.getCreated(), result.get(0).getCreated());
//    }
//}
