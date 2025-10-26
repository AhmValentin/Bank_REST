package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Стандартизированный ответ об ошибке")
public record ErrorResponse (
        @Schema(
                description = "URI пути, по которому произошла ошибка",
                example = "/api/cards/123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String uri,

        @Schema(
                description = "HTTP статус код и его текстовое представление",
                example = "500 INTERNAL SERVER ERROR",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String errorStatus,

        @Schema(
                description = "Пользовательское сообщение об ошибке",
                example = "Сервис временно недоступен. Повторите попытку позже",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String errorMessage,

        @Schema(
                description = "Детальное техническое описание ошибки для разработчиков",
                example = "Request timed out while waiting for a response from the upstream service.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String errorDetails,

        @Schema(
                description = "Временная метка возникновения ошибки в формате ISO 8601",
                example = "2024-09-03T09:50:05Z",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "date-time"
        )
        LocalDateTime timestamp
) {}
