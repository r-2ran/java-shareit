package ru.practicum.booking.exception;

public class NoSuchBookingFound extends RuntimeException {
    public NoSuchBookingFound(String message) {
        super(message);
    }
}
