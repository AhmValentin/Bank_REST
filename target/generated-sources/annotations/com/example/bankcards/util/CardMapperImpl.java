package com.example.bankcards.util;

import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.ResponseDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.Status;
import com.example.bankcards.entity.user.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-26T22:15:28+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class CardMapperImpl implements CardMapper {

    @Override
    public ResponseDto toDto(Card card) {
        if ( card == null ) {
            return null;
        }

        String cardHolder = null;
        UUID id = null;
        LocalDate expirationDate = null;
        BigDecimal balance = null;
        Status status = null;

        cardHolder = cardUserFullName( card );
        id = card.getId();
        expirationDate = card.getExpirationDate();
        balance = card.getBalance();
        status = card.getStatus();

        String maskedCardNumber = card.getMaskedCardNumber();

        ResponseDto responseDto = new ResponseDto( id, maskedCardNumber, cardHolder, expirationDate, balance, status );

        return responseDto;
    }

    @Override
    public List<ResponseDto> toDtoList(List<Card> cards) {
        if ( cards == null ) {
            return null;
        }

        List<ResponseDto> list = new ArrayList<ResponseDto>( cards.size() );
        for ( Card card : cards ) {
            list.add( toDto( card ) );
        }

        return list;
    }

    @Override
    public Card toResponse(CreateCardRequest request) {
        if ( request == null ) {
            return null;
        }

        Card card = new Card();

        card.setUser( map( request.userId() ) );
        card.setCardNumber( request.cardNumber() );
        card.setExpirationDate( request.expirationDate() );
        card.setBalance( request.balance() );

        card.setStatus( Status.ACTIVE );

        return card;
    }

    private String cardUserFullName(Card card) {
        if ( card == null ) {
            return null;
        }
        User user = card.getUser();
        if ( user == null ) {
            return null;
        }
        String fullName = user.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }
}
