package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Запрос на создание новой банковской карты")
public record CreateCardRequest(
        @Schema(
                description = "Номер банковской карты (16 цифр)",
                example = "1234567812345678",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 16,
                maxLength = 16
        )
        @NotBlank(message = "Card number is required")
        @Size(min = 16, max = 16)
        String cardNumber,

        @Schema(
                description = "Дата истечения срока действия карты",
                example = "2025-12-31",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Expiration date is required")
        @Future(message = "Expiration date must be in the future")
        LocalDate expirationDate,

        @Schema(
                description = "Начальный баланс карты",
                example = "1000.00",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "0"
        )
        @NotNull(message = "Balance is required")
        @PositiveOrZero(message = "Balance must be positive or zero")
        BigDecimal balance,

        @Schema(
                description = "UUID пользователя-владельца карты",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid"
        )
        @NotNull(message = "User ID is required")
        UUID userId
) {}
