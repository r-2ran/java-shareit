package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("list of users: {}", userService.getAllUsers());
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable int userId) {
        log.debug("get user: {}", userService.getUserById(userId));
        return userService.getUserById(userId);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug("added user: {}", user);
        return userService.addUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.debug("deleted user: {}", userService.getUserById(userId));
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable int userId, @RequestBody User user) {
        log.debug("updated user: {}", user);
        return userService.updateUser(userId, user);
    }
}
