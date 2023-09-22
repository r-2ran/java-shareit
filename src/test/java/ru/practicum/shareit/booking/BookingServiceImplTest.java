package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.NoSuchBookingFound;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.exception.NoSuchItemFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private LocalDateTime date = LocalDateTime.now();
    private User owner = new User(1L, "owner", "owner@mail.ru");
    private User boooker = new User(2L, "booker", "requestor@mail.ru");
    private User user = new User(3L, "user", "user@mail.ru");
    private Item item = new Item(1L, "item", "decsription", true,
            owner, null);
    private Item itemNotAvailavle = new Item(1L, "item", "decsription", false,
            owner, null);
    private Booking booking = new Booking(1L, date.minusDays(5), date.plusDays(5), item, boooker,
            BookingStatus.WAITING);
    private BookingDtoInput input = new BookingDtoInput(1L, date.plusHours(5),
            date.plusDays(5));

    @Test
    void addBookingNotAvalable() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemNotAvailavle));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final BookingException exception = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.addBooking(input, 99L));
        assertEquals("cannot book, item id = 1 is already booked", exception.getMessage());

    }

    @Test
    void addBookingNoTUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final NoSuchUserFound noSuchUserFound = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.addBooking(input, 99L));
        assertEquals("no such user id =99", noSuchUserFound.getMessage());
    }

    @Test
    void addBookingNoTItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NoSuchItemFound noSuchUserFound = Assertions.assertThrows(
                NoSuchItemFound.class,
                () -> bookingService.addBooking(input, 99L));
        assertNull(noSuchUserFound.getMessage());
    }

    @Test
    void addBookingOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.addBooking(input, 1L));
        assertEquals("owner id = 1 cannot be booker", e.getMessage());
    }

    @Test
    void addBookingWrongDate() {
        BookingDtoInput bookingDto = input;
        bookingDto.setStart(date.plusDays(10));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final BookingException e = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.addBooking(bookingDto, 1L));
        assertEquals("end cannot be before start", e.getMessage());
    }

    @Test
    void addBookingEqualDates() {
        BookingDtoInput bookingDto = input;
        bookingDto.setStart(date.plusDays(10));
        bookingDto.setEnd(date.plusDays(10));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final BookingException e = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.addBooking(bookingDto, 1L));
        assertEquals("start and end cannot be equal", e.getMessage());
    }


    @Test
    void addBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        assertEquals(booking.getId(), bookingService.addBooking(input, boooker.getId()).getId());
    }


    @Test
    void updateBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        assertEquals(BookingStatus.APPROVED, bookingService.updateBooking(booking.getId(),
                owner.getId(), true).getStatus());
    }

    @Test
    void updateBookingNotBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final NoSuchBookingFound e = Assertions.assertThrows(
                NoSuchBookingFound.class,
                () -> bookingService.updateBooking(1L, 99L, true));
        assertEquals("no booking id = 1", e.getMessage());
    }

    @Test
    void updateBookingNotUser() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.updateBooking(1L, 99L, true));
        assertEquals("no such user id =99", e.getMessage());
    }

    @Test
    void updateBookingNotOwner() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(boooker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final NoSuchBookingFound e = Assertions.assertThrows(
                NoSuchBookingFound.class,
                () -> bookingService.updateBooking(1L, 2L, true));
        assertEquals("user id = 2 have not access to approving", e.getMessage());
    }

    @Test
    void updateBookingApproved() {
        Booking booking1 = booking;
        booking1.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(boooker));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        final BookingException e = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.updateBooking(1L, 1L, true));
        assertEquals("booking id = 1 is already approved", e.getMessage());
    }

    @Test
    void updateBookingRejected() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        assertEquals(BookingStatus.REJECTED, bookingService.updateBooking(booking.getId(),
                owner.getId(), false).getStatus());
    }

    @Test
    void getBookingByIdNotBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));

        final NoSuchBookingFound e = Assertions.assertThrows(
                NoSuchBookingFound.class,
                () -> bookingService.getBookingById(1L, 1L));
        assertEquals("no booking id = 1", e.getMessage());
    }

    @Test
    void getBookingByIdNotUser() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingById(1L, 99L));
        assertEquals("no such user id =99", e.getMessage());
    }

    @Test
    void getBookingByIdNotBookig() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        final NoSuchBookingFound e = Assertions.assertThrows(
                NoSuchBookingFound.class,
                () -> bookingService.getBookingById(1L, 2L));
        assertEquals("no booking id = 1 and user id = 2", e.getMessage());
    }


    @Test
    void getBookingById() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));
        assertEquals(booking.getId(), bookingService.getBookingById(booking.getId(), boooker.getId()).getId());
    }

    @Test
    void getBookingByBookerWaiting() {
        when(userRepository.findById(boooker.getId()))
                .thenReturn(Optional.ofNullable(boooker));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                boooker.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(boooker.getId(),
                "WAITING", 0, 5).size());
    }

    @Test
    void getBookingByBookerWrongState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(boooker));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        final BookingException e = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.getBookingByBooker(1L, "VSE", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @Test
    void getBookingByBookerRejected() {
        when(userRepository.findById(boooker.getId()))
                .thenReturn(Optional.ofNullable(boooker));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                boooker.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(boooker.getId(),
                "REJECTED", 0, 5).size());
    }

    @Test
    void getBookingByBookerAll() {
        when(userRepository.findById(boooker.getId()))
                .thenReturn(Optional.ofNullable(boooker));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                boooker.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(boooker.getId(),
                "ALL", 0, 5).size());
    }

    @Test
    void getBookingByOwnerAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                boooker.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(1L,
                "ALL", 0, 5).size());
    }

    @Test
    void getBookingByBookerBadPage() {
        when(userRepository.findById(boooker.getId()))
                .thenReturn(Optional.ofNullable(boooker));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                boooker.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final BookingException e = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.getBookingByBooker(2L, "ALL", -5, 5));
        assertEquals("bad page params", e.getMessage());
    }

    @Test
    void getBookingByOwnerBadPage() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                boooker.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final BookingException e = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.getBookingByBooker(1L, "ALL", -5, 5));
        assertEquals("bad page params", e.getMessage());
    }

    @Test
    void getBookingByBookerNotUser() {
        when(userRepository.findById(boooker.getId()))
                .thenThrow(new NoSuchUserFound("no such user id =99"));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                boooker.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByBooker(99L, "ALL", 0, 5));
        assertEquals("no such user id =99", e.getMessage());
    }

    @Test
    void getBookingByOwnerNotUser() {
        when(userRepository.findById(owner.getId()))
                .thenThrow(new NoSuchUserFound("no such user id =99"));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                boooker.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByBooker(99L, "ALL", 0, 5));
        assertEquals("no such user id =99", e.getMessage());
    }

    @Test
    void getBookingByBookerCurrent() {
        when(userRepository.findById(boooker.getId()))
                .thenReturn(Optional.ofNullable(boooker));

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                boooker.getId(), date.plusDays(15), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(boooker.getId(),
                "CURRENT", 0, 5).size());
    }


    @Test
    void getBookingByBookerPast() {
        when(userRepository.findById(boooker.getId()))
                .thenReturn(Optional.ofNullable(boooker));

        when(bookingRepository.findAllByBookerIdAndEndIsBefore(
                boooker.getId(), date.plusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(boooker.getId(),
                "PAST", 0, 5).size());
    }

    @Test
    void getBookingByBookerFuture() {
        when(userRepository.findById(boooker.getId()))
                .thenReturn(Optional.ofNullable(boooker));

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(
                boooker.getId(), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(boooker.getId(),
                "FUTURE", 0, 5).size());
    }

    @Test
    void getBookingByOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        assertEquals(1, bookingService.getBookingByOwner(owner.getId(),
                "ALL", 0, 5).size());
    }

    @Test
    void getBookingByOwnerNoUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByOwner(99L, "ALL", 0, 5));
        assertEquals("no such user id =99", e.getMessage());
    }

    @Test
    void getBookingByOwnerWaiting() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(
                owner.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByOwner(owner.getId(),
                "WAITING", 0, 5).size());
    }

    @Test
    void getBookingByOwnerRejected() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(
                owner.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByOwner(owner.getId(),
                "REJECTED", 0, 5).size());
    }

    @Test
    void getBookingByOwnerCurrent() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                owner.getId(), date.plusDays(15), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByOwner(owner.getId(),
                "CURRENT", 0, 5).size());
    }

    @Test
    void getBookingByOwnerCurrentNotUser() {
        when(userRepository.findById(owner.getId()))
                .thenThrow(new NoSuchUserFound("not found"));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                owner.getId(), date.plusDays(15), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByOwner(owner.getId(),
                        "CURRENT", 0, 5));
        assertEquals("not found", e.getMessage());
    }

    @Test
    void getBookingByOwnerPast() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(
                owner.getId(), date.plusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByOwner(owner.getId(),
                "PAST", 0, 5).size());
    }

    @Test
    void getBookingByOwnerPastNotUser() {
        when(userRepository.findById(owner.getId()))
                .thenThrow(new NoSuchUserFound("not found"));

        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(
                owner.getId(), date.plusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByOwner(owner.getId(),
                        "PAST", 0, 5));
        assertEquals("not found", e.getMessage());
    }

    @Test
    void getBookingByOwnerFuture() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(
                owner.getId(), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByOwner(owner.getId(),
                "FUTURE", 0, 5).size());
    }

    @Test
    void getBookingByOwnerFutureNotUser() {
        when(userRepository.findById(owner.getId()))
                .thenThrow(new NoSuchUserFound("not found"));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(
                owner.getId(), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByOwner(owner.getId(),
                        "FUTURE", 0, 5));
        assertEquals("not found", e.getMessage());
    }

    @Test
    void getBookingByOwnerFutureWrong() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(
                owner.getId(), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final BookingException e = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.getBookingByOwner(owner.getId(),
                        "VSE", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }
}