package ru.practicum.shareit.user.exception;

public class AlreadyExistSuchUser extends RuntimeException {
    public AlreadyExistSuchUser(String message) {
        super(message);
    }
}
