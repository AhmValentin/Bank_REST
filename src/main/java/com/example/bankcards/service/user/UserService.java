package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.dto.user.UpdateUserRequest;
import com.example.bankcards.dto.user.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto createUser(CreateUserRequest request);
    List<UserDto> getAllUsers();
    UserDto getUserById(UUID id);
    UserDto updateUser(UUID id, UpdateUserRequest request);
    void deleteUser(UUID id);

}
