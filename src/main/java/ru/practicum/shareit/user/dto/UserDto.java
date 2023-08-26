package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
public class UserDto {
    int id;
    @NotBlank
    String name;
    @Email
    @NotBlank
    String email;
}
