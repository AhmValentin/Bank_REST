package com.example.bankcards.controller;

import com.example.bankcards.dto.card.BlockCardRequest;
import com.example.bankcards.dto.card.BlockCardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.ResponseDto;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.entity.card.Status;
import com.example.bankcards.service.card.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CardControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CardController cardController;

    private UUID cardId;
    private UUID userId;
    private ResponseDto responseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();

        responseDto = new ResponseDto(
                cardId,
                "**** **** **** 1234",
                "John Doe",
                LocalDate.of(2026, 12, 31),
                BigDecimal.valueOf(1000),
                Status.ACTIVE
        );
    }

    @Test
    void getAllCards_success() {
        when(cardService.getAllCards()).thenReturn(List.of(responseDto));

        List<ResponseDto> result = cardController.getAllCards();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(responseDto);
    }

    @Test
    void getUserCards_success() {
        when(authentication.getName()).thenReturn("test@example.com");
        Page<ResponseDto> page = new PageImpl<>(List.of(responseDto));
        when(cardService.getUserCards("test@example.com", 0, 10)).thenReturn(page);

        Page<ResponseDto> result = cardController.getUserCards(authentication, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(responseDto);
    }

    @Test
    void createCard_success() {
        UUID userId = UUID.randomUUID();
        CreateCardRequest request = new CreateCardRequest(
                "1234-5678-9012-3456",
                LocalDate.now().plusYears(2),
                BigDecimal.valueOf(500),
                userId
        );

        doNothing().when(cardService).createCard(request);

        cardController.createCard(request);

        verify(cardService).createCard(request);
    }


    @Test
    void updateCardStatus_success() {
        when(cardService.updateCardStatus(cardId, Status.BLOCKED)).thenReturn(responseDto);

        ResponseDto result = cardController.updateCardStatus(cardId, Status.BLOCKED);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void deleteCard_success() {
        doNothing().when(cardService).delete(cardId);

        ResponseEntity<Void> response = cardController.deleteCard(cardId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void deleteCard_notFound() {
        doThrow(new RuntimeException("Card not found")).when(cardService).delete(cardId);

        ResponseEntity<Void> response = cardController.deleteCard(cardId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void requestCardBlock_success() {
        BlockCardRequest request = new BlockCardRequest("Lost card", "test");
        BlockCardResponse blockResponse = new BlockCardResponse("success", null, null, cardId, null);

        when(cardService.requestCardBlock(cardId, request, userId)).thenReturn(blockResponse);

        ResponseEntity<BlockCardResponse> response = cardController.requestCardBlock(cardId, request, userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(blockResponse);
    }

    @Test
    void requestCardBlock_failure() {
        BlockCardRequest request = new BlockCardRequest("Lost card", "test");

        when(cardService.requestCardBlock(cardId, request, userId)).thenThrow(new RuntimeException("Cannot block"));

        ResponseEntity<BlockCardResponse> response = cardController.requestCardBlock(cardId, request, userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().status()).isEqualTo("error");
        assertThat(response.getBody().message()).isEqualTo("Cannot block");
    }

    @Test
    void getBalance_success() {
        when(authentication.getName()).thenReturn("test@example.com");
        Page<ResponseDto> page = new PageImpl<>(List.of(responseDto));
        when(cardService.getUserCards("test@example.com", 0, 10)).thenReturn(page);

        ResponseEntity<?> response = cardController.getBalance(authentication, 0, 10);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(responseDto.balance());
    }

    @Test
    void getBalance_failure() {
        when(authentication.getName()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = cardController.getBalance(authentication, 0, 10);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Ошибка: Error");
    }

    @Test
    void transfer_success() {
        TransferRequest request = new TransferRequest(cardId, cardId, BigDecimal.valueOf(100));
        when(authentication.getName()).thenReturn("test@example.com");
        when(cardService.transfer("test@example.com", request)).thenReturn("Success");

        ResponseEntity<String> response = cardController.transfer(request, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Success");
    }

    @Test
    void transfer_failure() {
        TransferRequest request = new TransferRequest(cardId, cardId, BigDecimal.valueOf(100));
        when(authentication.getName()).thenReturn("test@example.com");
        when(cardService.transfer("test@example.com", request)).thenThrow(new RuntimeException("Insufficient funds"));

        ResponseEntity<String> response = cardController.transfer(request, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Insufficient funds");
    }
}
