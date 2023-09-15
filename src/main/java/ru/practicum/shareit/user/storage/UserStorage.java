package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;

@Component
public interface UserStorage {
    User getUserById(long userId);

    User addUser(User user);

    User updateUser(int userId, User user);

    void deleteUser(int userId);

    List<User> getAllUsers();

    HashMap<Long, User> getHashMapUser();
}
