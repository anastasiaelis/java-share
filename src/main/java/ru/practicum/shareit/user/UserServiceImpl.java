package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
        //User currentUser = userRepository.getById(userDto.getId());
        //     .orElseThrow(() -> new NotFoundException("Пользователя с " + userDto.getId() + " не существует")
        //   );
        //if (userDto.getName() == null) {
        //  userDto.setName(currentUser.getName());
        /// }
        //if (userDto.getEmail() == null) {
        //  userDto.setEmail(currentUser.getEmail());
        //} else {
        // if (userDto.getEmail().equals(currentUser.getEmail())) {
        //       throw new AlreadyExistException("Ошибка обновления пользователя с email " + userDto.getEmail());
        //     }
        //   }

        // return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));

        final User u = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found " + userDto.getId()));
        final User user = UserMapper.toUser(userDto);
        if (user.getName() != null && !user.getName().isBlank()) {
            u.setName(user.getName());
        }
        if (user.getEmail() == null) {
            u.setEmail(user.getEmail());
        } else {
            if (user.getEmail().equals(u.getEmail())) {
                throw new AlreadyExistException("Ошибка обновления пользователя с email " + userDto.getEmail());
            }

        }
        userRepository.save(u);
        return UserMapper.toUserDto(u);
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