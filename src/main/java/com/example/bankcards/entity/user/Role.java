package com.example.bankcards.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Роли пользователей в системе", enumAsRef = true)
public enum Role {
    @Schema(description = "Администратор системы - полный доступ ко всем функциям")
    ADMIN,

    @Schema(description = "Обычный пользователь - базовый доступ к функциям")
    USER
}
