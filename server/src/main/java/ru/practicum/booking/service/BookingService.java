package ru.practicum.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoInput;

import java.util.List;

@Service
public interface BookingService {
    BookingDto addBooking(BookingDtoInput bookingDto, Long userId);

    BookingDto updateBooking(Long bookingId, Long userId, boolean approve);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingByBooker(Long userId, String state,
                                        int from, int size);

    List<BookingDto> getBookingByOwner(Long userId, String state,
                                       int from, int size);
}
