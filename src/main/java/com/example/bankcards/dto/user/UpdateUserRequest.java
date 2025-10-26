package com.example.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Запрос на обновление данных пользователя")
public record UpdateUserRequest(
        @Schema(
                description = "Новый номер телефона в международном формате",
                example = "+79991234567",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String phoneNumber,

        @Schema(
                description = "Новое имя пользователя",
                example = "Иван",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(min = 1, max = 50, message = "Имя должно содержать от 1 до 50 символов")
        String firstName,

        @Schema(
                description = "Новая фамилия пользователя",
                example = "Иванов",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(min = 1, max = 50, message = "Фамилия должна содержать от 1 до 50 символов")
        String lastName,

        @Schema(
                description = "Новая дата рождения пользователя",
                example = "1990-01-15",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                format = "date"
        )
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate
) {}
