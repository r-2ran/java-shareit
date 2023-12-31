package ru.practicum.user.service;

import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Component
public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);
}
