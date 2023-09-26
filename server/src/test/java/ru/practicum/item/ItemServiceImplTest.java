package ru.practicum.item;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.exception.BookingException;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.item.service.ItemServiceImpl;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.exception.NoSuchUserFound;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

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
    private Item itemUpd = new Item(10L, "item", "description", false, owner,
            request);
    private Item itemNoRequest = new Item(2L, "item", "description", true, owner,
            null);
    private ItemDto itemDto = new ItemDto(1L, "item", "description", true, owner,
            request.getId());
    private ItemDto itemDtoNoReq = new ItemDto(2L, "item", "description", true, owner,
            null);
    private Booking booking = new Booking(1L, date.minusDays(10), date.minusDays(5), item,
            booker, BookingStatus.APPROVED);
    private Comment comment = new Comment(1L, "text", item, booker, date);
    private CommentDto commentDto = new CommentDto("text");
    private Booking lastBooking = new Booking();
    private Booking last = new Booking(1L, date.minusDays(5), date.plusDays(5), item, booker,
            BookingStatus.APPROVED);
    private Booking nextBooking = new Booking();
    private Booking next = new Booking(1L, date.plusDays(10), date.plusDays(15), item, booker,
            BookingStatus.APPROVED);

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
    void addItemNoRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(itemNoRequest);
        assertEquals(itemService.addItem(2L, itemDtoNoReq).getId(), 2);
    }

    @Test
    void addItemNotUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemService.addItem(99L, itemDto));

        Assertions.assertEquals("no such user id = 99, so cannot add item", exception.getMessage());
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
        updatedItem.setIsAvailable(Boolean.valueOf(false));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);

        assertEquals(itemService
                        .updateItem(owner.getId(), updated, item.getId()).getName(),
                updatedItem.getName());

        assertEquals(itemService
                        .updateItem(owner.getId(), updated, item.getId()).getDescription(),
                updatedItem.getDescription());
        assertEquals(itemService
                        .updateItem(1L, updated, item.getId()).getAvailable(),
                updatedItem.getIsAvailable());
    }

    @Test
    void updateItemNoValue() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        ItemDto updated = new ItemDto();
        updated.setId(1L);

        when(itemRepository.save(any()))
                .thenReturn(item);
        assertEquals(itemService.updateItem(1L, updated, 1L).getId(),
                itemDto.getId());
    }

    @Test
    void updateItemNotUser() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemDto updated = new ItemDto("new name", "new description");
        Item updatedItem = item;
        updatedItem.setName("new name");
        updatedItem.setDescription("new description");

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemService.updateItem(1L, updated, 99L));

        Assertions.assertEquals("no such user id = 99", exception.getMessage());
    }

    @Test
    void getItemById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertEquals(itemService.getItemById(1L, 1L).getId(), 1);

    }

    @Test
    void getItemByIdOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(commentRepository.findAll())
                .thenReturn(List.of(comment));

        when(bookingRepository
                .findAllByItemIdAndStartIsBeforeAndStatusNot(anyLong(), any(LocalDateTime.class), any(Sort.class),
                        any(BookingStatus.class)))
                .thenReturn(List.of(last));

        when(bookingRepository
                .findAllByItemIdAndStartIsAfterAndStatusNot(anyLong(), any(LocalDateTime.class), any(Sort.class),
                        any(BookingStatus.class)))
                .thenReturn(List.of(next));

        assertEquals(itemService.getItemById(1L, 1L).getId(), 1);

    }

    @Test
    void getItemByIdNotUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemService.getItemById(1L, 99L));

        Assertions.assertEquals("no user id = 99", exception.getMessage());
    }

    @Test
    void searchItem() {
        when(itemRepository.search(anyString()))
                .thenReturn(List.of(item));

        assertEquals(itemService.searchItem("desc", requestor.getId()).size(), 1);
    }

    @Test
    void searchBlankTextItem() {
        when(itemRepository.search(""))
                .thenReturn(new ArrayList<>());

        assertEquals(itemService.searchItem("", requestor.getId()).size(), 0);
    }


    @Test
    void getAllItemsByUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertEquals(List.of(item).size(), itemService.getAllItemsByUser(anyLong()).size());
    }

    @Test
    void getAllItemsByOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findAllByItemIdAndStartIsBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findAllByItemIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(nextBooking));

        assertEquals(List.of(item).size(), itemService.getAllItemsByUser(anyLong()).size());
    }


    @Test
    void getAllItemsByUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemService.getAllItemsByUser(99L));
        Assertions.assertEquals("no user id = 99", exception.getMessage());
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

    @Test
    void addCommentNotUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findAllByBookerIdAndEndIsBeforeAndStatusEquals(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        final NoSuchUserFound exception = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> itemService.addComment(1L, commentDto, 99L));
        Assertions.assertEquals("no user id = 99", exception.getMessage());
    }

    @Test
    void addCommentNotItemRented() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findAllByBookerIdAndEndIsBeforeAndStatusEquals(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());

        final BookingException exception = Assertions.assertThrows(
                BookingException.class,
                () -> itemService.addComment(1L, commentDto, 99L));
        Assertions.assertEquals("item id = 1 by user id = 99 has not been rented", exception.getMessage());
    }

    @Test
    void bookingAdd() {
        when(bookingRepository.findAllByItemIdAndStartIsBeforeAndStatusNot(anyLong(), any(LocalDateTime.class),
                any(Sort.class), any(BookingStatus.class)))
                .thenReturn(List.of(last, lastBooking));
        when(bookingRepository
                .findAllByItemIdAndStartIsAfterAndStatusNot(anyLong(), any(LocalDateTime.class),
                        any(Sort.class), any(BookingStatus.class)))
                .thenReturn(List.of(next, nextBooking));

        assertEquals(itemService.addBooking(1L, itemDto).getId(), 1L);
    }

    @Test
    void bookingAddEmpty() {
        when(bookingRepository.findAllByItemIdAndStartIsBeforeAndStatusNot(anyLong(), any(LocalDateTime.class),
                any(Sort.class), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepository
                .findAllByItemIdAndStartIsAfterAndStatusNot(anyLong(), any(LocalDateTime.class),
                        any(Sort.class), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());

        assertEquals(itemService.addBooking(1L, itemDto).getId(), 1L);
    }
}