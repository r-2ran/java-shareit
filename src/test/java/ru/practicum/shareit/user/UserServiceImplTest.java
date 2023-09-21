package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.AlreadyExistSuchUser;
import ru.practicum.shareit.user.exception.NoSuchUserFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private User user = new User(1L, "user", "user@mail.ru");
    private UserDto userDto = new UserDto(1L, "user", "user@mail.ru");

    @Test
    void getAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        assertEquals(userDto.getId(), userService.getAllUsers().get(0).getId());
        assertEquals(userDto.getName(), userService.getAllUsers().get(0).getName());
        assertEquals(userDto.getEmail(), userService.getAllUsers().get(0).getEmail());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        assertEquals(userDto.getId(), userService.getUserById(1L).getId());
        assertEquals(userDto.getName(), userService.getUserById(1L).getName());
        assertEquals(userDto.getEmail(), userService.getUserById(1L).getEmail());

    }

    @Test
    void getUserByIdNot() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchUserFound("not found"));

        final NoSuchUserFound e = Assertions.assertThrows(
                NoSuchUserFound.class,
                () -> userService.getUserById(99L));
        assertEquals("not found", e.getMessage());

    }

    @Test
    void addUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        assertEquals(userDto.getId(), userService.addUser(userDto).getId());
        assertEquals(userDto.getName(), userService.addUser(userDto).getName());
        assertEquals(userDto.getEmail(), userService.addUser(userDto).getEmail());
    }

    @Test
    void addUserExist() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new AlreadyExistSuchUser("exist"));

        final AlreadyExistSuchUser e = Assertions.assertThrows(
                AlreadyExistSuchUser.class,
                () -> userService.addUser(userDto));
        assertEquals("exist", e.getMessage());
    }

    @Test
    void updateUser() {
        User updated = new User(1L, "new name", "user@mail.ru");
        UserDto input = new UserDto("new name", "user@mail.ru");
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(updated);
        UserDto updatedUserDto = new UserDto(1L, "new name", "user@mail.ru");

        assertEquals(updatedUserDto.getId(), userService.updateUser(1L, input).getId());
    }
}