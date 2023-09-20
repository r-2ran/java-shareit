//package ru.practicum.shareit.item;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.CommentRepository;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.request.repository.ItemRequestRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
//public class ItemRepositoryTest {
//
//    @Autowired
//    private ItemRepository itemRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ItemRequestRepository itemRequestRepository;
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    @Test
//    void searchTest() {
//        User user = new User(1L, "user", "user@mail.ru");
//        Item item = new Item("name", "description", true);
//        item.setOwner(user);
//        itemRepository.save(item);
//        List<Item> items = itemRepository.search("description");
//        assertThat(items.size(), equalTo(1));
//    }
//
//    @Test
//    void findAllByOwnerIdTest() {
//        User user = new User(1L, "user", "user@mail.ru");
//        Item item = new Item("name", "description", true);
//        item.setOwner(user);
//        itemRepository.save(item);
//        List<Item> items = itemRepository.findAllByOwnerId(user.getId());
//        assertThat(items.size(), equalTo(1));
//    }
//
//    @Test
//    void findAllByRequestIdTest() {
//        User user = new User(1L, "user", "user@mail.ru");
//        User user2 = new User(2L, "user2", "user2@mail.ru");
//        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder().description("item request descr")
//                .requestor(user2).created(LocalDateTime.now()).build());
//        itemRepository.save(Item.builder().name("name").description("description").available(true)
//                .owner(user).request(itemRequest).build());
//        assertThat(itemRepository.findAllByRequestId(itemRequest.getId()).size(), equalTo(1));
//    }
//
//    @Test
//    void findAllCommentByItemIdTest() {
//        User user = userRepository.save(User.builder().name("name").email("email@email.com").build());
//        User user2 = userRepository.save(User.builder().name("name2").email("email2@email.com").build());
//        Item item = itemRepository.save(Item.builder().name("name").description("description")
//                .available(true).owner(user).build());
//        commentRepository.save(Comment.builder().text("text of comment").item(item).author(user2)
//                .created(LocalDateTime.now()).build());
//        assertThat(commentRepository.findAllByItemId(item.getId()).size(), equalTo(1));
//    }
//}
