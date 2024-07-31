package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.exceptions.UserEmailConflictException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @InjectMocks
    private UserController controller;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private UserDto userDto;
    private List<UserDto> userDtos;

    @Test
    @SneakyThrows
    void createUserWhenUserIsValid() {
        UserDto userDtoToCreate = UserDto.builder()
                .email("email@email.com")
                .name("name")
                .build();

        when(userService.add(userDtoToCreate)).thenReturn(userDtoToCreate);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToCreate), result);
    }

    @Test
    @SneakyThrows
    void createUserWheUserEmailIsNotValidShouldReturnBadRequest() {
        UserDto userDtoToCreate = UserDto.builder()
                .email("email.com")
                .name("name")
                .build();

        when(userService.add(userDtoToCreate)).thenReturn(userDtoToCreate);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDtoToCreate);
    }

    @Test
    @SneakyThrows
    void createUserWheNameIsNotValidShouldReturnBadRequest() {
        UserDto userDtoToCreate = UserDto.builder()
                .email("email@email.com")
                .name("     ")
                .build();

        when(userService.add(userDtoToCreate)).thenReturn(userDtoToCreate);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDtoToCreate);
    }

    @Test
    @SneakyThrows
    void updateUserWhenUserIsValid() {
        Long userId = 0L;
        UserDto userDtoToUpdate = UserDto.builder()
                .email("update@update.com")
                .name("update")
                .build();

        when(userService.update(userId, userDtoToUpdate)).thenReturn(userDtoToUpdate);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateUserWheUserEmailIsNotValidShouldReturnBadRequest() {
        Long userId = 0L;
        UserDto userDtoToUpdate = UserDto.builder()
                .email("update.com")
                .name("update")
                .build();

        when(userService.update(userId, userDtoToUpdate)).thenReturn(userDtoToUpdate);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDtoToUpdate);
    }

    @Test
    @SneakyThrows
    void get() {
        long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).findById(userId);
    }

    @Test
    @SneakyThrows
    void findAll() {
        List<UserDto> usersDtoToExpect = List.of(UserDto.builder().name("name").email("email@email.com").build());

        when(userService.findAll()).thenReturn(usersDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(usersDtoToExpect), result);
    }

    @Test
    @SneakyThrows
    void delete() {
        long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }

    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        userDto = makeUserDto();
        userDtos = List.of(userDto);
    }

    @Test
    void createUserWithThrowable() throws Exception {
        setUp();
        when(userService.add(any()))
                .thenThrow(UserEmailConflictException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    private UserDto makeUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Пётр")
                .email("some@email.com")
                .build();
    }
}