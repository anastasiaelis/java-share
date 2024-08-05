package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@Validated
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    //private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            throw new AlreadyExistException("Пользователь с email " + user.getEmail() + "уже существует!");
        }
        return UserMapper.toUserDto(user);

    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User currentUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователя с " + userDto.getId() + " не существует")
                );
        if (userDto.getEmail() == null) {
            userDto.setEmail(currentUser.getEmail());
        } else {
            if (userRepository.existsByEmail(userDto.getEmail()) && !userDto.getEmail().equals(currentUser.getEmail())) {
                throw new AlreadyExistException("Ошибка обновления пользователя с email " + userDto.getEmail());
            }
        }
        if (userDto.getName() == null) {
            userDto.setName(currentUser.getName());
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        if (userRepository.existsById(id)) {
            log.info("Информация о пользователе " + id + " успешно получена!");
            return UserMapper.toUserDto(userRepository.findById(id).get());
        }
        throw new NotFoundException("Пользователя с таким id не существует!");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long id) {

    }
}