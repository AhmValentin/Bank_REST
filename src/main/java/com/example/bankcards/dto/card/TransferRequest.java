package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Запрос на перевод средств между банковскими картами")
public record TransferRequest(
        @Schema(
                description = "Уникальный идентификатор карты-отправителя",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid"
        )
        @NotNull(message = "ID карты отправителя обязателен")
        UUID fromCardId,

        @Schema(
                description = "Уникальный идентификатор карты-получателя",
                example = "123e4567-e89b-12d3-a456-426614174001",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid"
        )
        @NotNull(message = "ID карты получателя обязателен")
        UUID toCardId,

        @Schema(
                description = "Сумма перевода. Должна быть положительной",
                example = "100.50",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "decimal"
        )
        @NotNull(message = "Сумма перевода обязательна")
        @Positive(message = "Сумма перевода должна быть положительной")
        BigDecimal amount
) {}
