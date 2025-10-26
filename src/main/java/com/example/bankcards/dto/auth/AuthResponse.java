package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с токенами аутентификации и информацией о пользователе")
public record AuthResponse(
        @Schema(
                description = "JWT access token для авторизации запросов",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String token,

        @Schema(
                description = "JWT refresh token для обновления access token",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String refreshToken,

        @Schema(
                description = "Email аутентифицированного пользователя",
                example = "user@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email"
        )
        String email,

        @Schema(
                description = "Роль аутентифицированного пользователя",
                example = "USER",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"USER", "ADMIN"}
        )
        String role,

        @Schema(
                description = "Полное имя пользователя",
                example = "Иван Иванов",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String fullName
){}
