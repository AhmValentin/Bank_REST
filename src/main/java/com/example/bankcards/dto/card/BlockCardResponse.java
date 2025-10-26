package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Ответ на запрос блокировки карты")
public record BlockCardResponse(
        @Schema(
                description = "Статус операции",
                example = "success",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String status,

        @Schema(
                description = "Сообщение о результате операции",
                example = "Запрос на блокировку карты успешно создан",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message,

        @Schema(
                description = "Уникальный идентификатор запроса на блокировку",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String requestId,

        @Schema(
                description = "UUID карты, которую блокируют",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid"
        )
        UUID cardId,

        @Schema(
                description = "UUID пользователя, инициировавшего блокировку",
                example = "123e4567-e89b-12d3-a456-426614174001",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid"
        )
        UUID userId
) {}
