package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на блокировку карты")
public record BlockCardRequest(
        @Schema(
                description = "Причина блокировки карты",
                example = "Утеря карты",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = 255
        )
        @NotBlank(message = "Причина блокировки не может быть пустой")
        @Size(min = 1, max = 255, message = "Причина блокировки должна содержать от 1 до 255 символов")
        String reason,

        @Schema(
                description = "Дополнительный комментарий к блокировке",
                example = "Карта была утеряна в общественном транспорте",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 500
        )
        @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
        String comment
) {}
