package ru.practicum.shareit.user.model;

import lombok.Getter;

@Getter
public class UserErrorResponse {
    private final String error;

    public UserErrorResponse(String error) {
        this.error = error;
    }
}
