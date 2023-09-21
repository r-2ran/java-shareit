package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.fromPage;


@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User owner;
    private User booker;
    private ItemRequest request;
    private Item item;
    private LocalDateTime date = LocalDateTime.now();
    private Booking booking;
    private Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void create() {
        booker = userRepository.save(new User(null, "user", "user@mail.com"));
        owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        item = itemRepository.save(new Item(null, "item", "description",
                true, owner, null));
        booking = bookingRepository.save(new Booking(null,
                date.minusHours(5),
                date.plusDays(3),
                item, booker, BookingStatus.WAITING));
    }

    @AfterEach
    void delete() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdAndStartIsAfter() {
        List<Booking> bookings = fromPage(bookingRepository
                .findAllByBookerIdAndStartIsAfter(booker.getId(), date.minusHours(10), pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByBookerId() {
        List<Booking> bookings = fromPage(bookingRepository
                .findAllByBookerId(booker.getId(), pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        List<Booking> bookings = fromPage(
                bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(booker.getId(),
                        date, date, pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByBookerIdAndEndIsBefore() {
        List<Booking> bookings = fromPage(
                bookingRepository.findAllByBookerIdAndEndIsBefore(booker.getId(), date.plusDays(10), pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByBookerIdAndStatusEquals() {
        List<Booking> bookings = fromPage(
                bookingRepository.findAllByBookerIdAndStatusEquals(booker.getId(), BookingStatus.WAITING,
                        pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemOwnerId() {
        List<Booking> bookings = fromPage(
                bookingRepository.findAllByItemOwnerId(owner.getId(), pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfter() {
        List<Booking> bookings = fromPage(
                bookingRepository.findAllByItemOwnerIdAndStartIsAfter(owner.getId(), date.minusHours(10),
                        pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter() {
        List<Booking> bookings = fromPage(
                bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(owner.getId(),
                        date, date.plusDays(2), pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBefore() {
        List<Booking> bookings = fromPage(
                bookingRepository.findAllByItemOwnerIdAndEndIsBefore(owner.getId(),
                        date.plusDays(10), pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatusEquals() {
        List<Booking> bookings = fromPage(
                bookingRepository.findAllByItemOwnerIdAndStatusEquals(owner.getId(),
                        BookingStatus.WAITING, pageable));
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemId() {
        List<Booking> bookings =
                bookingRepository.findAllByItemId(item.getId(), Sort.unsorted());
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeAndStatusEquals() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndEndIsBeforeAndStatusEquals(booker.getId(), date.plusDays(10),
                        BookingStatus.APPROVED);
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemIdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndStartIsAfter(item.getId(), date.minusHours(10), Sort.unsorted());
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemIdAndStartIsBefore() {
        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndStartIsBefore(item.getId(), date, Sort.unsorted());
        assertNotEquals(bookings.size(), 0);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByItemIdAndEndIsAfter() {
        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndEndIsAfter(item.getId(), date.plusDays(10), Sort.unsorted());
        assertEquals(bookings.size(), 0);
    }
}