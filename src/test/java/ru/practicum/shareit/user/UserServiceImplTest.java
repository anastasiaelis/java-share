package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserMapper userMapper;


    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("my@email.com")
            .build();

    @Test
    void addNewUserReturnUserDto() {
        User userToSave = User.builder().id(1L).name("name").email("my@email.com").build();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUserDto = userService.addUser(userDto);

        assertEquals(userDto, actualUserDto);
        verify(userRepository).save(userToSave);
    }

//    @Test
//    void updateUserTest() {
//        User user = User.builder().id(1L).name("name").email("my@email.com").build();
//        Long userId = user.getId();
//        userRepository.save(user);
//        UserDto fieldsToUpdate = new UserDto();
//        fieldsToUpdate.setId(1L);
//        fieldsToUpdate.setEmail("updated@example.com");
//        fieldsToUpdate.setName("Updated User");
//      User usere= userRepository.getUserById(userId);
//      //  when(userRepository.getUserById(userId)).thenReturn(user);
//      //  when(userRepository.save(user)).thenReturn(user);
//
//        UserDto updatedUserDto = userService.update(fieldsToUpdate);
//        assertNotNull(updatedUserDto);
//      assertEquals("Updated User", updatedUserDto.getName());
//       assertEquals("updated@example.com", updatedUserDto.getEmail());
//    }


    @Test
    void findUserByIdWhenUserFound() {
        long userId = 1L;
        User expectedUser = User.builder().id(1L).name("name").email("my@email.com").build();
        when(userRepository.existsById(expectedUser.getId()))
                .thenReturn(true);
        when(userRepository.findById(expectedUser.getId()))
                .thenReturn(Optional.of(expectedUser));
        //  when(userService.getUser(expectedUser.getId()))
        //      .thenReturn(userMapper.toUserDto(expectedUser));

        assertThat(userService.getUser(expectedUser.getId()))
                .isNotNull()
                .usingRecursiveComparison();
        //  .isEqualTo(expectedUser);
    }

    @Test
    void findUserByIdWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getUser(100L));
    }

    @Test
    void findAllUsersTest() {
        List<User> expectedUsers = List.of(new User());
        List<UserDto> expectedUserDto = expectedUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsersDto = userService.getUsers();

        assertEquals(actualUsersDto.size(), 1);
        assertEquals(actualUsersDto, expectedUserDto);
    }

    @Test
    void deleteUser() {
        long userId = 0L;
        userService.delete(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}