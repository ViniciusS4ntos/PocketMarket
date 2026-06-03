package com.pocketmarket.cards.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CardResponseDTO(

        UUID id,
        String externalId,
        String name,
        String setId,
        String setName,
        String number,
        String rarity,
        String imageSmallUrl,
        String imageLargeUrl,
        String description,
        String source,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}