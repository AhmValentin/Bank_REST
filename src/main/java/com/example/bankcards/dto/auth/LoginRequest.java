package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на аутентификацию пользователя")
public record LoginRequest(
        @Schema(
                description = "Email пользователя",
                example = "john.smith@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email"
        )
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,

        @Schema(
                description = "Пароль пользователя",
                example = "user123",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "password"
        )
        @NotBlank(message = "Пароль обязателен")
        String password
) {}
