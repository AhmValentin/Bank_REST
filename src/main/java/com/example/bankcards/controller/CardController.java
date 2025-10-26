package com.example.bankcards.controller;

import com.example.bankcards.dto.card.BlockCardRequest;
import com.example.bankcards.dto.card.BlockCardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.ResponseDto;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.entity.card.Status;
import com.example.bankcards.service.card.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
@Tag(name = "Card Management", description = "API для управления банковскими картами")
@SecurityRequirement(name = "bearerAuth")
public class CardController {
    private final CardService cardService;

    @Operation(summary = "Получить все карты (админ)", description = "Возвращает список всех карт в системе. Только для администраторов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка карт"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/admin")
    public List<ResponseDto> getAllCards() {
        return cardService.getAllCards();
    }

    @Operation(summary = "Получить карты пользователя", description = "Возвращает пагинированный список карт текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка карт"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/user")
    public Page<ResponseDto> getUserCards(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userEmail = authentication.getName();
        return cardService.getUserCards(userEmail, page, size);
    }

    @Operation(summary = "Создать новую карту", description = "Создает новую банковскую карту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    @PostMapping
    public void createCard(@Valid @RequestBody CreateCardRequest request) {
        cardService.createCard(request);
    }

    @Operation(summary = "Обновить статус карты", description = "Обновляет статус карты по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус карты успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PatchMapping("/{cardId}/status")
    public ResponseDto updateCardStatus(@PathVariable UUID cardId,
                                        @RequestParam Status status) {
        return cardService.updateCardStatus(cardId, status);
    }

    @Operation(summary = "Удалить карту", description = "Удаляет карту по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        try {
            cardService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Запрос на блокировку карты", description = "Создает запрос на блокировку карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на блокировку успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка при создании запроса на блокировку"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PostMapping("/{cardId}/block-request")
    public ResponseEntity<BlockCardResponse> requestCardBlock(
            @PathVariable UUID cardId,
            @RequestBody BlockCardRequest request,
            @AuthenticationPrincipal UUID userId){
        try {
            BlockCardResponse response = cardService.requestCardBlock(cardId, request, userId);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            BlockCardResponse errorResponse = new BlockCardResponse(
                    "error",
                    e.getMessage(),
                    null,
                    cardId,
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(summary = "Получить общий баланс", description = "Рассчитывает общий баланс всех карт пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно рассчитан"),
            @ApiResponse(responseCode = "400", description = "Ошибка при расчете баланса")
    })
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication authentication,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        try {
            String userEmail = authentication.getName();
            Page<ResponseDto> userCards = cardService.getUserCards(userEmail, page, size);
            BigDecimal totalBalance = userCards.stream()
                    .map(ResponseDto::balance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return ResponseEntity.ok(totalBalance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @Operation(summary = "Перевод средств", description = "Выполняет перевод средств между картами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Ошибка при выполнении перевода")
    })
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestBody TransferRequest request,
            Authentication authentication) {

        try {
            String userEmail = authentication.getName();
            String result = cardService.transfer(userEmail, request);
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
