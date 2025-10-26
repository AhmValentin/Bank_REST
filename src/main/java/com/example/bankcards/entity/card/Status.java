package com.example.bankcards.entity.card;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статусы банковской карты", enumAsRef = true)
public enum Status {
    @Schema(description = "Активная карта - может использоваться для всех операций")
    ACTIVE,

    @Schema(description = "Заблокированная карта - все операции запрещены")
    BLOCKED,

    @Schema(description = "Карта с истекшим сроком действия - требует замены")
    EXPIRED
}
