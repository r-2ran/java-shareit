package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.util.List;

@Service
public interface BookingService {
    BookingDto addBooking(BookingDtoInput bookingDto, long userId);

    void deleteBooking(long bookingId);

    BookingDto updateBooking(long bookingId, long userId, boolean approve);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getBookingByBooker(long userId, String state,
                                        int from, int size);

    List<BookingDto> getBookingByOwner(long userId, String state,
                                       int from, int size);
}
