package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
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
    public UserDto update(UserDto userDto) {
        User currentUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователя с " + userDto.getId() + " не существует")
                );
        if (userDto.getName() == null) {
            userDto.setName(currentUser.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(currentUser.getEmail());
        } else {
            if (userRepository.existsByEmail(userDto.getEmail()) && userDto.getEmail().equals(currentUser.getEmail())) {
                throw new AlreadyExistException("Ошибка обновления пользователя с email " + userDto.getEmail());
            }
        }

        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + id + " не существует")
                );
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}