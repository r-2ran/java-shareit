package ru.practicum.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoInput;
import ru.practicum.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    BookingDto addBooking(@Valid @RequestBody BookingDtoInput bookingDtoInput,
                          @RequestHeader(USER_ID) Long userId) {
        return bookingService.addBooking(bookingDtoInput, userId);
    }

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(@PathVariable Long bookingId,
                              @RequestHeader(USER_ID) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    BookingDto updateBooking(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID) Long userId,
                             @RequestParam boolean approved) {
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping
    List<BookingDto> getBookingsByBooker(@RequestHeader(USER_ID) Long userId,
                                         @RequestParam(name = "state",
                                                 required = false, defaultValue = "ALL") String state,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<BookingDto> getBookingsByOwner(@RequestHeader(USER_ID) Long userId,
                                        @RequestParam(name = "state",
                                                required = false,
                                                defaultValue = "ALL") String state,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingByOwner(userId, state, from, size);
    }
}
