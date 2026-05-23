package com.pocketmarket.cards.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CardResponseDTO(

        UUID id,

        String name,

        String setName,

        String rarity,

        String condition,

        BigDecimal price,

        Integer stock,

        String imageUrl,

        String description
) {
}