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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private Item item = new Item(1L, "item", "decsription", true,
            owner, null);
    private Booking booking = new Booking(1L, date.minusDays(5), date.plusDays(5), item, boooker,
            BookingStatus.WAITING);
    private BookingDtoInput input = new BookingDtoInput(item.getId(), date.plusHours(5),
            date.plusDays(5));

    @Test
    void addBookingNoUser() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchUserFound("no such user"));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        final NoSuchUserFound noSuchUserFound = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.addBooking(input, 99));
        assertEquals("no such user", noSuchUserFound.getMessage());

    }

//    @Test
//    void addBookingNoItem() {
//        Item item1 = new Item("name", "descp", true);
//        item1.setId(99L);
//        Booking returnBooking = booking;
//        returnBooking.setItem(item1);
//        when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.of(owner));
//
//        when(itemRepository.findById(anyLong()))
//                .thenThrow(new NoSuchItemFound("not found"));
//
//        when(bookingRepository.save(any()))
//                .thenReturn(booking);
//
//        BookingDtoInput bookingDto = input;
//        bookingDto.setItemId(99L);
//
//        final NoSuchItemFound e = Assertions.assertThrows(
//                NoSuchItemFound.class,
//                () -> bookingService.addBooking(bookingDto, 1L));
//        assertEquals("no such user", e.getMessage());
//    }

//    @Test
//    void addBookingException() {
//        Item notAvailb = new Item("name", "someitem", false);
//        notAvailb.setId(5L);
//
//        when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.of(owner));
//
//        when(itemRepository.findById(anyLong()))
//                .thenReturn(Optional.of(notAvailb));
//        BookingDtoInput bookingDtoInput = input;
//        bookingDtoInput.setItemId(5L);
//        final BookingException e = Assertions.assertThrows(
//                BookingException.class,
//                () -> bookingService.addBooking(input, 1L));
//        assertEquals("no such user", e.getMessage());
//    }


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
        when(bookingRepository.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        assertEquals(1, bookingService.getBookingByBooker(owner.getId(),
                "ALL", 0, 5).size());
    }

    @Test
    void getBookingByOwnerWaiting() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                owner.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(owner.getId(),
                "WAITING", 0, 5).size());
    }

    @Test
    void getBookingByOwnerRejected() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(
                owner.getId(), BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(owner.getId(),
                "REJECTED", 0, 5).size());
    }

    @Test
    void getBookingByOwnerCurrent() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                owner.getId(), date.plusDays(15), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(owner.getId(),
                "CURRENT", 0, 5).size());
    }

    @Test
    void getBookingByOwnerCurrentNotUser() {
        when(userRepository.findById(owner.getId()))
                .thenThrow(new NoSuchUserFound("not found"));

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                owner.getId(), date.plusDays(15), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByBooker(owner.getId(),
                        "CURRENT", 0, 5));
        assertEquals("not found", e.getMessage());
    }

    @Test
    void getBookingByOwnerPast() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByBookerIdAndEndIsBefore(
                owner.getId(), date.plusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(owner.getId(),
                "PAST", 0, 5).size());
    }

    @Test
    void getBookingByOwnerPastNotUser() {
        when(userRepository.findById(owner.getId()))
                .thenThrow(new NoSuchUserFound("not found"));

        when(bookingRepository.findAllByBookerIdAndEndIsBefore(
                owner.getId(), date.plusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByBooker(owner.getId(),
                        "PAST", 0, 5));
        assertEquals("not found", e.getMessage());
    }

    @Test
    void getBookingByOwnerFuture() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(
                owner.getId(), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        assertEquals(0, bookingService.getBookingByBooker(owner.getId(),
                "FUTURE", 0, 5).size());
    }

    @Test
    void getBookingByOwnerFutureNotUser() {
        when(userRepository.findById(owner.getId()))
                .thenThrow(new NoSuchUserFound("not found"));

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(
                owner.getId(), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> bookingService.getBookingByBooker(owner.getId(),
                        "FUTURE", 0, 5));
        assertEquals("not found", e.getMessage());
    }

    @Test
    void getBookingByOwnerFutureWrong() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(
                owner.getId(), date.minusDays(15), Pageable.unpaged()))
                .thenReturn(List.of(booking));

        final BookingException e = Assertions.assertThrows(
                BookingException.class,
                () -> bookingService.getBookingByBooker(owner.getId(),
                        "VSE", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }
}