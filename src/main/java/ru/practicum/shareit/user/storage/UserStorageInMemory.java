package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.AlreadyExistSuchUser;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserStorageInMemory implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private final Set<String> usersEmails = new HashSet<>();
    private int generatedId = 1;

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public User addUser(User user) throws AlreadyExistSuchUser, ValidationException {
        if (!isDuplicate(user)) {
            user.setId(generatedId++);
            users.put(user.getId(), user);
            usersEmails.add(user.getEmail());
            return users.get(user.getId());

        } else {
            throw new AlreadyExistSuchUser(String.format("email %s already exist", user.getEmail()));
        }
    }

    @Override
    public void deleteUser(int userId) {
        usersEmails.remove(getUserById(userId).getEmail());
        users.remove(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(int userId, User user) throws NoSuchUserFound, AlreadyExistSuchUser {
        if (users.containsKey(userId)) {
            if (user.getName() != null) {
                users.get(userId).setName(user.getName());
            }

            if (user.getEmail() != null) {
                if (usersEmails.add(user.getEmail()) || user.getEmail().equals(getUserById(userId).getEmail())) {
                    users.get(userId).setEmail(user.getEmail());
                } else {
                    throw new AlreadyExistSuchUser(String.format("email %s already exist", user.getEmail()));
                }
            }
            user.setId(userId);
            return users.get(userId);
        } else {
            throw new NoSuchUserFound(String.format("no such user id = %d", userId));
        }

    }

    private boolean isDuplicate(User user) {
        boolean res = false;
        for (User user1 : users.values()) {
            if (user.getEmail().equals(user1.getEmail())) {
                res = true;
                break;
            }
        }
        return res;
    }

    @Override
    public HashMap<Integer, User> getHashMapUser() {
        return users;
    }
}
