package com.pocketmarket.usercards.dto.response;

import com.pocketmarket.cards.Card;
import com.pocketmarket.enums.CardCondition;
import com.pocketmarket.enums.UserCardStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserCardResponse(
        UUID id,
        Card card,
        UUID ownerId,
        String ownerName,
        CardCondition condition,
        UserCardStatus status,
        String proofImageUrl,
        LocalDateTime acquiredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
