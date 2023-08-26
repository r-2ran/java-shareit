package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;

@Component
public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(int userId);

    User addUser(User user);

    User updateUser(int userId, User user);

    void deleteUser(int userId);

    HashMap<Integer, User> userHashMap();
}
