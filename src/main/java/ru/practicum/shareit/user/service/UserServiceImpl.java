package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtos(userStorage.getAllUsers());
    }

    @Override
    public UserDto getUserById(int userId) {
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public User addUser(User user) {
        return userStorage.addUser(user);
    }


    @Override
    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public User updateUser(int userId, User user) {
        return userStorage.updateUser(userId, user);
    }

    @Override
    public HashMap<Integer, User> userHashMap() {
        return userStorage.getHashMapUser();
    }
}
