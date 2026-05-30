package com.pocketmarket.usercards.dto.response;

import com.pocketmarket.cards.Card;
import com.pocketmarket.enums.CardCondition;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserCardResponse(
        UUID id,
        Card card,
        User owner,
        CardCondition condition,
        UserCardStatus status,
        String proofImageUrl,
        LocalDateTime acquiredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
