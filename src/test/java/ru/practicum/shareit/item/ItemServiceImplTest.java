package ru.practicum.shareit.item;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceImplTest {
    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private LocalDateTime date = LocalDateTime.now();

    private User requestor = new User(1L, "user", "user@mail.ru");
    private User owner = new User(2L, "owner", "owner@mail.ru");
    private UserDto ownerDto = new UserDto(2L, "owner", "owner@mail.ru");

    private User booker = new User(3L, "booker", "booker@mail.ru");
    private ItemRequest request = new ItemRequest(1L, "description", requestor,
            date);

    private Item item = new Item(1L, "item", "description", true, owner,
            request);
    private ItemDto itemDto = new ItemDto(1L, "item", "description", true, owner,
            request.getId());
    private Booking booking = new Booking(1L, date.minusDays(10), date.minusDays(5), item,
            booker, BookingStatus.APPROVED);
    private Comment comment = new Comment(1L, "text", item, booker, date);
    private CommentDto commentDto = new CommentDto("text");

    @Test
    void addItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        assertEquals(itemService.addItem(owner.getId(), itemDto).getId(), itemDto.getId());
    }

    @Test
    void addItemNotUser() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchUserFound("not found"));

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemService.addItem(99L, itemDto));

        Assertions.assertEquals("not found", exception.getMessage());
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        ItemDto updated = new ItemDto("new name", "new description");
        Item updatedItem = item;
        updatedItem.setName("new name");
        updatedItem.setDescription("new description");
        when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);

        assertEquals(itemService
                        .updateItem(owner.getId(), updated, item.getId()).getName(),
                updatedItem.getName());

        assertEquals(itemService
                        .updateItem(owner.getId(), updated, item.getId()).getDescription(),
                updatedItem.getDescription());

    }

    @Test
    void updateItemNotUser() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchUserFound("not found"));

        ItemDto updated = new ItemDto("new name", "new description");
        Item updatedItem = item;
        updatedItem.setName("new name");
        updatedItem.setDescription("new description");

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemService.updateItem(1L, updated, 99L));

        Assertions.assertEquals("not found", exception.getMessage());
    }

//    @Test
//    void updateItemNotItem() {
//        when(itemRepository.findById(anyLong()))
//                .thenThrow(new NoSuchItemFound("not found"));
//
//        when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(owner));
//
//        ItemDto updated = new ItemDto("new name", "new description");
//        Item updatedItem = item;
//        updatedItem.setName("new name");
//        updatedItem.setDescription("new description");
//
//        final NoSuchItemFound exception = Assertions.assertThrows(
//                NoSuchItemFound.class,
//                () -> itemService.updateItem(99L, updated, 1L));
//        Assertions.assertEquals("not found", exception.getMessage());
//    }

    @Test
    void getItemById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertEquals(itemService.getItemById(1L, 1L).getId(), 1);

    }

    @Test
    void getItemByIdNotUser() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchUserFound("not found"));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemService.getItemById(1L, 99L));

        Assertions.assertEquals("not found", exception.getMessage());
    }

//    @Test
//    void getItemByIdNotItem() {
//        when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(requestor));
//
//        when(itemRepository.findById(anyLong()))
//                .thenThrow(new NoSuchItemFound("not found"));
//
//        final NoSuchItemFound exception = Assertions.assertThrows(
//                NoSuchItemFound.class,
//                () -> itemService.getItemById(99L, requestor.getId()));
//
//        Assertions.assertEquals("not found", exception.getMessage());
//    }

    @Test
    void searchItem() {
        when(itemRepository.search(anyString()))
                .thenReturn(List.of(item));

        assertEquals(itemService.searchItem("desc", requestor.getId()).size(), 1);
    }

    @Test
    void searchBlankTextItem() {
        when(itemRepository.search(anyString()))
                .thenReturn(new ArrayList<>());

        assertEquals(itemService.searchItem("desc", requestor.getId()).size(), 0);
    }


    @Test
    void getAllItemsByUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertEquals(List.of(item).size(), itemService.getAllItemsByUser(anyLong()).size());
    }

    @Test
    void addComment() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findAllByBookerIdAndEndIsBeforeAndStatusEquals(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        assertEquals(comment.getId(), itemService.addComment(
                item.getId(), commentDto, booker.getId()).getId());
    }
}