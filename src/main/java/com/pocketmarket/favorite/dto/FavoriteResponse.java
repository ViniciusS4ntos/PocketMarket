package com.pocketmarket.favorite.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FavoriteResponse(
        UUID favoriteId,
        UUID cardId,
        String cardName,
        String setName,
        String rarity,
        String condition,
        BigDecimal price,
        String imageUrl
) {}