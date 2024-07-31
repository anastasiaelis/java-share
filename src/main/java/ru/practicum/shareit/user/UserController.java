package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ColoredCRUDLogger;
import ru.practicum.shareit.user.markers.Create;
import ru.practicum.shareit.user.markers.Update;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j

public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        UserDto zss=userDto;
        //log.info("Запрос на добавление пользователя {}", userDto);
        //return userService.add(userDto);

        ColoredCRUDLogger.logPost("/users", userDto.toString());
      var result= userService.add(userDto);
       ColoredCRUDLogger.logPostComplete("/users", result.toString());
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
    public UserDto update(@PathVariable Long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя id = {}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Delete - запрос на удаление пользователя id = {}", userId);
        userService.delete(userId);
    }
}