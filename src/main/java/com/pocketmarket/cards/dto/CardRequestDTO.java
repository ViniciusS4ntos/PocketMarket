package com.pocketmarket.cards.dto;

import com.pocketmarket.enums.CardCondition;
import com.pocketmarket.enums.CardRarity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CardRequestDTO(

        @NotBlank
        String name,

        String setName,

        @NotNull
        CardRarity rarity,

        @NotNull
        CardCondition condition,

        @NotNull
        BigDecimal price,

        Integer stock,

        String imageUrl,

        String description
) {
}