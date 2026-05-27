package com.pocketmarket.collection.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CollectionResponse(
        UUID collectionId,
        UUID cardId,
        String cardName,
        String setName,
        String rarity,
        String condition,
        BigDecimal price,
        String imageUrl,
        Integer quantity,
        LocalDateTime addedAt
) {}