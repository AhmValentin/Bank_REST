package com.example.bankcards.service.card;


import com.example.bankcards.dto.card.BlockCardRequest;
import com.example.bankcards.dto.card.BlockCardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.ResponseDto;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.entity.card.Status;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface CardService {
    List<ResponseDto> getAllCards();
    void createCard(CreateCardRequest request);
    ResponseDto updateCardStatus(UUID cardId, Status newStatus);
    Page<ResponseDto> getUserCards(String userEmail, int page, int size);
    void delete(UUID id);
    BlockCardResponse requestCardBlock(UUID cardId, BlockCardRequest request, UUID userId);
    String transfer(String userEmail, TransferRequest request);
}
