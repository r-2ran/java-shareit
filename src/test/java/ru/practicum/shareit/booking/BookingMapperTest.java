package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.*;

class BookingMapperTest {
    private LocalDateTime date = LocalDateTime.now();
    private User booker = new User(1L, "booker", "booker@mail.ru");
    private User owner = new User(2L, "owner", "owner@mail.ru");
    private Item item = new Item(1L, "item", "description", true,
            owner, new ItemRequest());
    private Booking booking = new Booking(1L, date.minusHours(5), date.plusDays(5), item, booker,
            BookingStatus.WAITING);
    private BookingDto bookingDto = new BookingDto(1L, date.minusHours(5), date.plusDays(5), item, booker,
            BookingStatus.WAITING);
    private BookingDtoInput bookingDtoInput = new BookingDtoInput(1L, date.minusHours(5), date.plusDays(5));


    @Test
    void toBookingFromDtoTest() {
        Booking res = toBookingFromDto(bookingDto);
        assertEquals(res.getId(), bookingDto.getId());
        assertEquals(res.getStatus(), bookingDto.getStatus());
    }

    @Test
    void toBookingDtoTest() {
        BookingDto res = toBookingDto(booking);
        assertEquals(res.getId(), booking.getId());
    }

    @Test
    void toBookingDtoListTest() {
        List<BookingDto> res = toBookingDtoList(List.of(booking));
        assertEquals(res.get(0).getId(), booking.getId());
    }

    @Test
    void fromBookingTest() {
        BookingForItemDto res = fromBooking(booking);
        assertEquals(res.getId(), booking.getId());
    }

    @Test
    void fromPageTest() {
        List<Booking> res = fromPage(List.of(booking));
        assertEquals(res.get(0).getId(), booking.getId());
    }
}