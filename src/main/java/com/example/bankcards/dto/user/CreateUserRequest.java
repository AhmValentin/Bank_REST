package com.example.bankcards.dto.user;

import com.example.bankcards.entity.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Запрос на создание нового пользователя")
public record CreateUserRequest(
        @Schema(
                description = "Email пользователя",
                example = "user@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email"
        )
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,

        @Schema(
                description = "Номер телефона в международном формате",
                example = "+79991234567",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Номер телефона обязателен")
        String phoneNumber,

        @Schema(
                description = "Пароль пользователя",
                example = "securePassword123",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "password"
        )
        @NotBlank(message = "Пароль обязателен")
        String password,

        @Schema(
                description = "Имя пользователя",
                example = "Иван",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Имя обязательно")
        @Size(max = 50, message = "Имя не должно превышать 50 символов")
        String firstName,

        @Schema(
                description = "Фамилия пользователя",
                example = "Иванов",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 50, message = "Фамилия не должна превышать 50 символов")
        String lastName,

        @Schema(
                description = "Роль пользователя в системе",
                implementation = Role.class,
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "USER"
        )
        @NotNull(message = "Роль обязательна")
        Role role,

        @Schema(
                description = "Дата рождения пользователя",
                example = "1990-01-15",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "date"
        )
        @NotNull(message = "Дата рождения обязательна")
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate
) {}
