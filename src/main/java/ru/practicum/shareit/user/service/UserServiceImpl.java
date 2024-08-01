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
            throw new AlreadyExistException("Пользователь с email " + user.getEmail() + "!");
        }
        return UserMapper.toUserDto(user);

    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с " + id + " не существует")
                );
        String name = userDto.getName();
        if (name != null && !name.isBlank()) {
            userDto.setName(name);
        }
        String email = userDto.getEmail();
        if (email != null && !email.isBlank()) {
            userDto.setEmail(email);
            if (userRepository.existsByEmail(userDto.getEmail()) && !userDto.getEmail().equals(currentUser.getEmail())) {
                throw new AlreadyExistException("Ошибка обновления пользователя с email " + userDto.getEmail());
            }
        }
        return UserMapper.toUserDto(currentUser);
    }

    @Override
    @Transactional
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с " + id + " не существует")
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