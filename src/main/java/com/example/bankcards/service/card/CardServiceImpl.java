package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.BlockCardRequest;
import com.example.bankcards.dto.card.BlockCardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.ResponseDto;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.Status;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private  final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    @Override
    public List<ResponseDto> getAllCards(){
        List<Card> cards = cardRepository.findAll();
        return cardMapper.toDtoList(cards);
    }

    @Override
    public void createCard(CreateCardRequest request){
        Card card = cardMapper.toResponse(request);
        cardRepository.save(card);
    }

    @Override
    public ResponseDto updateCardStatus(UUID cardId, Status newStatus) {
        Card card = findCardById(cardId);
        card.setStatus(newStatus);
        Card savedCard = cardRepository.save(card);

        return cardMapper.toDto(savedCard);
    }

    private Card findCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Карта не найдена: " + cardId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponseDto> getUserCards(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + userEmail + " не найден"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Card> userCards = cardRepository.findByUser(user, pageable);
        return userCards.map(cardMapper::toDto);
    }

    public void delete(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new NotFoundException("Карта с id " + id + " не найдена");
        }
        cardRepository.deleteById(id);
    }

    @Override
    public BlockCardResponse requestCardBlock(UUID cardId, BlockCardRequest request, UUID userId){
        Card card = findCardById(cardId);
        if (card.getStatus() == Status.BLOCKED) {
            throw new BadRequestException("Карта уже заблокирована");
        }
        String requestId = System.currentTimeMillis() + "-" + cardId;
        return new BlockCardResponse(
                "pending",
                "Запрос на блокировку карты принят в обработку",
                requestId,
                cardId,
                userId
        );
    }

    public String transfer(String userEmail, TransferRequest request) {
        Card fromCard = cardRepository.findById(request.fromCardId())
                .orElseThrow(() -> new NotFoundException("Карта отправителя не найдена"));

        if (!fromCard.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("Карта не принадлежит данному пользователю");
        }

        Card toCard = cardRepository.findById(request.toCardId())
                .orElseThrow(() -> new NotFoundException("Карта получателя не найдена"));

        if (fromCard.getStatus() == Status.BLOCKED) {
            throw new BadRequestException("Карта отправителя заблокирована");
        }
        if (toCard.getStatus() == Status.BLOCKED) {
            throw new BadRequestException("Карта получателя заблокирована");
        }
        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new BadRequestException("Недостаточно средств на карте");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        return "Перевод " + request.amount() + " выполнен";
    }
}
