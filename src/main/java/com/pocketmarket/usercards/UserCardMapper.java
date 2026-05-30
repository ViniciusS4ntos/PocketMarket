package com.pocketmarket.usercards;

import com.pocketmarket.cards.Card;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
import jakarta.validation.Valid;

public class UserCardMapper {

    public static UserCard toEntity(@Valid UserCardRequest request, User user, Card card) {
        return UserCard.builder()
                .card(card)
                .owner(user)
                .condition(request.condition())
                .proofImageUrl(request.proofImageUrl())
                .build();
    }

    public static UserCardResponse toResponse(UserCard userCardSaved) {
        return new UserCardResponse(
                userCardSaved.getId(),
                userCardSaved.getCard(),
                userCardSaved.getOwner().getId(),
                userCardSaved.getOwner().getName(),
                userCardSaved.getCondition(),
                userCardSaved.getStatus(),
                userCardSaved.getProofImageUrl(),
                userCardSaved.getAcquiredAt(),
                userCardSaved.getCreatedAt(),
                userCardSaved.getUpdatedAt()
        );
    }
}
