package ru.practicum.user;

import org.junit.jupiter.api.Test;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.user.mapper.UserMapper.*;

class UserMapperTest {
    private User user = new User(1L, "name", "user@mail.ru");
    private UserDto userDto = new UserDto(1L, "name", "user@mail.ru");

    @Test
    void toUserDtoTest() {
        UserDto res = toUserDto(user);
        assertEquals(res.getId(), user.getId());
        assertEquals(res.getName(), user.getName());
        assertEquals(res.getEmail(), user.getEmail());
    }

    @Test
    void toUserTest() {
        User res = toUser(userDto);
        assertEquals(res.getId(), userDto.getId());
        assertEquals(res.getName(), userDto.getName());
        assertEquals(res.getEmail(), userDto.getEmail());
    }

    @Test
    void toUserDtosTest() {
        List<UserDto> res = toUserDtos(List.of(user));
        assertEquals(res.get(0).getId(), user.getId());
        assertEquals(res.get(0).getName(), user.getName());
        assertEquals(res.get(0).getEmail(), user.getEmail());
    }
}