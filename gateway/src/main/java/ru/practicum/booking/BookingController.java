package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAll(userId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOwner(@RequestHeader(USER_ID) Long userId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID) Long userId,
                                           @RequestBody @Valid BookingDto bookingDto) {
        return bookingClient.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) Long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PatchMapping
    public ResponseEntity<Object> updateBooking(@PathVariable Long bookingId,
                                                @RequestHeader(USER_ID) Long userId,
                                                @RequestParam(value = "approved") boolean approved) {
        return bookingClient.updateBooking(bookingId, userId, approved);
    }
}
