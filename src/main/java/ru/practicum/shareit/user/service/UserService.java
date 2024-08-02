package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    //Optional<UserDto> getUser(long id);

    UserDto getUser(Long id);

    @Transactional
    void delete(Long id);

    List<UserDto> getUsers();

    void deleteUser(long id);
}