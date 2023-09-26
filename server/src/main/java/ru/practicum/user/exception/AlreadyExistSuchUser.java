package ru.practicum.user.exception;

public class AlreadyExistSuchUser extends RuntimeException {
    public AlreadyExistSuchUser(String message) {
        super(message);
    }
}
