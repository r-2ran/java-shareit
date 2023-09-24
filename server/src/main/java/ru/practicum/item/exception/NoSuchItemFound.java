package ru.practicum.item.exception;

public class NoSuchItemFound extends RuntimeException {
    String message;

    public NoSuchItemFound(String message) {
        this.message = message;
    }
}
