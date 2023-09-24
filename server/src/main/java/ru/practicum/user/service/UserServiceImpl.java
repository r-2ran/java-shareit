package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.exception.NoSuchUserFound;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtos(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) throws NoSuchUserFound {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchUserFound(String.format("no user id = %d", userId));
        }
        return UserMapper.toUserDto(userRepository.findById(userId).get());
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User inDB = UserMapper.toUser(getUserById(userId));
        if (userDto.getName() != null) {
            inDB.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            inDB.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(inDB));
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
