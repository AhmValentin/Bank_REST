package com.example.bankcards.dto.card;

import com.example.bankcards.entity.card.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO для ответа с детальной информацией о банковской карте")
public record ResponseDto(
        @Schema(
                description = "Уникальный идентификатор карты в системе",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid"
        )
        UUID id,

        @Schema(
                description = "Маскированный номер карты для безопасного отображения",
                example = "************1111",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String maskedCardNumber,

        @Schema(
                description = "Полное имя держателя карты в верхнем регистре",
                example = "IVAN PETROV",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50
        )
        String cardHolder,

        @Schema(
                description = "Дата истечения срока действия в формате ГГГГ-ММ-ДД",
                example = "2025-12-31",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "date"
        )
        LocalDate expirationDate,

        @Schema(
                description = "Текущий баланс карты с точностью до двух знаков после запятой",
                example = "1500.75",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "decimal",
                minimum = "0"
        )
        BigDecimal balance,

        @Schema(
                description = "Текущий статус карты",
                implementation = Status.class,
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "ACTIVE"
        )
        Status status
) {}
