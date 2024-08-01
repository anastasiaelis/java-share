package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.markers.Create;
import ru.practicum.shareit.user.markers.Update;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя {}", userDto);
        return userService.add(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя id = {}", userId);
        return userService.findById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Запрос на получение списка всех пользователей");
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UserDto userDto,
                          @Positive @PathVariable Long userId) {
        log.info("Запрос на обновление пользователя id = {}", userDto.getId());
        userDto.setId(userId);
        return userService.update(userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Delete - запрос на удаление пользователя id = {}", userId);
        userService.delete(userId);
    }
}