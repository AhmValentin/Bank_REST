package com.example.bankcards.controller;

import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.dto.user.UpdateUserRequest;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private UserDto userDto;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();

        userDto = new UserDto(
                userId,
                "test@example.com",
                "1234567890",
                "John",
                "Doe",
                Role.USER,
                LocalDate.of(1990, 1, 1),
                "John Doe"
        );

        createRequest = new CreateUserRequest(
                "test@example.com",
                "1234567890",
                "password",
                "John",
                "Doe",
                Role.USER,
                LocalDate.of(1990, 1, 1)
        );

        updateRequest = new UpdateUserRequest(
                "0987654321",
                "Jane",
                "Smith",
                LocalDate.of(1992, 2, 2)
        );
    }

    @Test
    void createUser_success() {
        when(userService.createUser(createRequest)).thenReturn(userDto);

        ResponseEntity<?> response = userController.createUser(createRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(userDto);
    }

    @Test
    void createUser_failure() {
        when(userService.createUser(createRequest)).thenThrow(new RuntimeException("Email already exists"));

        ResponseEntity<?> response = userController.createUser(createRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Email already exists");
    }

    @Test
    void getAllUsers_success() {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        List<UserDto> result = userController.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(userDto);
    }

    @Test
    void getUserById_success() {
        when(userService.getUserById(userId)).thenReturn(userDto);

        ResponseEntity<?> response = userController.getUserById(userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(userDto);
    }

    @Test
    void getUserById_notFound() {
        when(userService.getUserById(userId)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<?> response = userController.getUserById(userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void updateUser_success() {
        when(userService.updateUser(userId, updateRequest)).thenReturn(userDto);

        ResponseEntity<?> response = userController.updateUser(userId, updateRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(userDto);
    }

    @Test
    void updateUser_failure() {
        when(userService.updateUser(userId, updateRequest)).thenThrow(new RuntimeException("Update failed"));

        ResponseEntity<?> response = userController.updateUser(userId, updateRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Update failed");
    }

    @Test
    void deleteUser_success() {
        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<?> response = userController.deleteUser(userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Пользователь удален");
    }

    @Test
    void deleteUser_failure() {
        doThrow(new RuntimeException("Delete failed")).when(userService).deleteUser(userId);

        ResponseEntity<?> response = userController.deleteUser(userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Delete failed");
    }
}

