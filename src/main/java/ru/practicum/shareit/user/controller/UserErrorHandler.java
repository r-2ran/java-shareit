package ru.practicum.shareit.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.AlreadyExistSuchUser;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.UserErrorResponse;

@RestControllerAdvice
public class UserErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public UserErrorResponse handleAlreadyExistSuchUser(final AlreadyExistSuchUser e) {
        return new UserErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UserErrorResponse handleNoSuchUserFound(final NoSuchUserFound e) {
        return new UserErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UserErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new UserErrorResponse(e.getMessage());
    }
}
