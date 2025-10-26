package com.example.bankcards.service;

import com.example.bankcards.dto.card.BlockCardRequest;
import com.example.bankcards.dto.card.BlockCardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.ResponseDto;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.Status;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.card.CardServiceImpl;
import com.example.bankcards.util.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private Card testCard;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@mail.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("hashedPassword");
        testUser.setRole(Role.USER);
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));

        testCard = new Card();
        testCard.setId(UUID.randomUUID());
        testCard.setUser(testUser);
        testCard.setStatus(Status.ACTIVE);
        testCard.setBalance(new BigDecimal("1000.00"));
        testCard.setCardNumber("1234567890123456");
        testCard.setExpirationDate(LocalDate.now().plusYears(3));
    }

    @Test
    void getAllCards_ReturnsMappedDtos() {
        List<Card> cards = List.of(testCard);

        ResponseDto dto = new ResponseDto(
                testCard.getId(),
                testCard.getMaskedCardNumber(),
                testCard.getUser().getFullName(),
                testCard.getExpirationDate(),
                testCard.getBalance(),
                testCard.getStatus()
        );

        when(cardRepository.findAll()).thenReturn(cards);
        when(cardMapper.toDtoList(cards)).thenReturn(List.of(dto));

        List<ResponseDto> result = cardService.getAllCards();

        assertThat(result).hasSize(1);
        ResponseDto actual = result.get(0);
        assertThat(actual.id()).isEqualTo(testCard.getId());
        assertThat(actual.maskedCardNumber()).isEqualTo(testCard.getMaskedCardNumber());
        assertThat(actual.cardHolder()).isEqualTo(testUser.getFullName());
        assertThat(actual.expirationDate()).isEqualTo(testCard.getExpirationDate());
        assertThat(actual.balance()).isEqualByComparingTo(testCard.getBalance());
        assertThat(actual.status()).isEqualTo(testCard.getStatus());

        verify(cardRepository).findAll();
    }

    @Test
    void createCard_SavesMappedEntity() {
        LocalDate futureDate = LocalDate.now().plusYears(1);
        BigDecimal balance = BigDecimal.TEN;

        CreateCardRequest req = new CreateCardRequest(
                testCard.getCardNumber(),
                futureDate,
                balance,
                testUser.getId()
        );

        Card newCard = new Card();
        when(cardMapper.toResponse(req)).thenReturn(newCard);

        cardService.createCard(req);

        verify(cardRepository).save(newCard);
    }


    @Test
    void updateCardStatus_UpdatesSuccessfully() {
        when(cardRepository.findById(testCard.getId())).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any())).thenReturn(testCard);

        ResponseDto dto = new ResponseDto(
                testCard.getId(),
                testCard.getMaskedCardNumber(),
                testCard.getUser().getFullName(),
                testCard.getExpirationDate(),
                testCard.getBalance(),
                Status.BLOCKED
        );
        when(cardMapper.toDto(any())).thenReturn(dto);

        ResponseDto result = cardService.updateCardStatus(testCard.getId(), Status.BLOCKED);

        assertThat(result.status()).isEqualTo(Status.BLOCKED);
        verify(cardRepository).save(testCard);
    }


    @Test
    void updateCardStatus_ThrowsIfNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.updateCardStatus(cardId, Status.BLOCKED))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Карта не найдена: " + cardId);
    }


    @Test
    void getUserCards_ReturnsPagedCards() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Card> cardPage = new PageImpl<>(List.of(testCard), pageable, 1);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cardRepository.findByUser(testUser, pageable)).thenReturn(cardPage);
        when(cardMapper.toDto(testCard)).thenReturn(new ResponseDto(
                testCard.getId(),
                testCard.getMaskedCardNumber(),
                testUser.getFullName(),
                testCard.getExpirationDate(),
                testCard.getBalance(),
                Status.ACTIVE
        ));

        Page<ResponseDto> result = cardService.getUserCards(testUser.getEmail(), 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(cardRepository).findByUser(testUser, pageable);
    }


    @Test
    void getUserCards_ThrowsIfUserNotFound() {
        String badEmail = "bad@mail.com";

        when(userRepository.findByEmail(badEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.getUserCards(badEmail, 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с email " + badEmail + " не найден");

        verify(userRepository, times(1)).findByEmail(badEmail);
        verifyNoInteractions(cardRepository);
    }

    @Test
    void requestCardBlock_ShouldReturnPendingResponse() {
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(Status.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        BlockCardRequest request = new BlockCardRequest("lost", "lost in park");

        BlockCardResponse response = cardService.requestCardBlock(cardId, request, userId);

        assertNotNull(response);
        assertEquals("pending", response.status());
        assertEquals("Запрос на блокировку карты принят в обработку", response.message());
        assertEquals(cardId, response.cardId());
        assertEquals(userId, response.userId());
        assertNotNull(response.requestId());

        verify(cardRepository).findById(cardId);
    }

    @Test
    void requestCardBlock_ThrowsIfAlreadyBlocked() {
        testCard.setStatus(Status.BLOCKED);
        when(cardRepository.findById(testCard.getId())).thenReturn(Optional.of(testCard));

        assertThatThrownBy(() -> cardService.requestCardBlock(testCard.getId(), new BlockCardRequest("test", "Some comment"), testUser.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Карта уже заблокирована");
    }

    @Test
    void transfer_Successful() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setId(UUID.randomUUID());
        Card sender = new Card();
        sender.setId(UUID.randomUUID());
        sender.setUser(user);
        sender.setBalance(BigDecimal.valueOf(1000));
        sender.setStatus(Status.ACTIVE);
        Card receiver = new Card();
        receiver.setId(UUID.randomUUID());
        receiver.setUser(new User());
        receiver.setBalance(BigDecimal.valueOf(500));
        receiver.setStatus(Status.ACTIVE);
        TransferRequest request = new TransferRequest(sender.getId(), receiver.getId(), BigDecimal.valueOf(200));

        when(cardRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(cardRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = cardService.transfer(user.getEmail(), request);

        assertEquals("Перевод 200 выполнен", result);
        assertEquals(BigDecimal.valueOf(800), sender.getBalance());
        assertEquals(BigDecimal.valueOf(700), receiver.getBalance());

        verify(cardRepository).save(sender);
        verify(cardRepository).save(receiver);
    }

}
