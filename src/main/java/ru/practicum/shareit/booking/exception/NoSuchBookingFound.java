package ru.practicum.shareit.booking.exception;

public class NoSuchBookingFound extends RuntimeException {
    public NoSuchBookingFound(String message) {
        super(message);
    }
}
