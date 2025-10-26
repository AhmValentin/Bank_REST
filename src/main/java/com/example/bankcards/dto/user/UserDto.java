package com.example.bankcards.dto.user;

import com.example.bankcards.entity.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO для ответа с информацией о пользователе")
public record UserDto(
        @Schema(
                description = "Уникальный идентификатор пользователя",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid"
        )
        UUID id,

        @Schema(
                description = "Email пользователя",
                example = "user@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email"
        )
        String email,

        @Schema(
                description = "Номер телефона пользователя",
                example = "+79991234567",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String phoneNumber,

        @Schema(
                description = "Имя пользователя",
                example = "Иван",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String firstName,

        @Schema(
                description = "Фамилия пользователя",
                example = "Иванов",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String lastName,

        @Schema(
                description = "Роль пользователя в системе",
                implementation = Role.class,
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "USER"
        )
        Role role,

        @Schema(
                description = "Дата рождения пользователя",
                example = "1990-01-15",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "date"
        )
        LocalDate birthDate,

        @Schema(
                description = "Полное имя пользователя (генерируется автоматически)",
                example = "Иван Иванов",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String fullName
) {}
