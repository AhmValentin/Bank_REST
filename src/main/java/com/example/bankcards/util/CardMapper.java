package com.example.bankcards.util;

import com.example.bankcards.config.MappingConfig;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.ResponseDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(config = MappingConfig.class)
public interface CardMapper {
    @Mapping(target = "maskedCardNumber", expression = "java(card.getMaskedCardNumber())")
    @Mapping(target = "cardHolder", source = "user.fullName")
    ResponseDto toDto(Card card);

    List<ResponseDto> toDtoList(List<Card> cards);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "status", constant = "ACTIVE")
    Card toResponse(CreateCardRequest request);

    default User map(UUID userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
