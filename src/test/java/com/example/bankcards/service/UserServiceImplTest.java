package com.example.bankcards.service;

import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.dto.user.UpdateUserRequest;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.user.UserServiceImpl;
import com.example.bankcards.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.USER);
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        userDto = new UserDto(
                userId,
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getBirthDate(),
                user.getFirstName() + " " + user.getLastName()
        );
    }

    @Test
    void createUser_success() {
        CreateUserRequest request = new CreateUserRequest(
                "test@example.com",
                "1234567890",
                "password",
                "John",
                "Doe",
                Role.USER,
                LocalDate.of(1990, 1, 1)
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(request);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void createUser_emailAlreadyExists_throwsException() {
        CreateUserRequest request = new CreateUserRequest(
                "test@example.com",
                "1234567890",
                "password",
                "John",
                "Doe",
                Role.USER,
                LocalDate.of(1990, 1, 1)
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(userDto);
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertThat(result).isEqualTo(userDto);
    }

    @Test
    void getUserById_notFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_success() {
        UpdateUserRequest request = new UpdateUserRequest(
                "0987654321",
                "Jane",
                "Smith",
                LocalDate.of(1992, 2, 2)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(1);
            u.setPhoneNumber(request.phoneNumber());
            u.setFirstName(request.firstName());
            u.setLastName(request.lastName());
            u.setBirthDate(request.birthDate());
            return null;
        }).when(userMapper).updateEntity(request, user);

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(userId, request);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_notFound_throwsException() {
        UpdateUserRequest request = new UpdateUserRequest(
                "0987654321",
                "Jane",
                "Smith",
                LocalDate.of(1992, 2, 2)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_notFound_throwsException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
    }
}
