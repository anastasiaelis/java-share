package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
//        return new UserDto(
//                user.getId(),
//                user.getName(),
//                user.getEmail());
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }
}