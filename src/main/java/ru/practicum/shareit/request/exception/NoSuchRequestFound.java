package ru.practicum.shareit.request.exception;

public class NoSuchRequestFound extends RuntimeException {
    public NoSuchRequestFound(String message) {
        super(message);
    }
}
