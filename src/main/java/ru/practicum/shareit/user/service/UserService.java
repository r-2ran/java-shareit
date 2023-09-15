package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;

@Component
public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);

    HashMap<Long, User> userHashMap();
}
