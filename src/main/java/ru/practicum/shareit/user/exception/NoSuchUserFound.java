package ru.practicum.shareit.user.exception;

public class NoSuchUserFound extends RuntimeException {
    public NoSuchUserFound(String message) {
        super(message);
    }
}
