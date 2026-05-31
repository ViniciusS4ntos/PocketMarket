package com.pocketmarket.cards.dto;

import jakarta.validation.constraints.NotBlank;

public record CardRequestDTO(

        @NotBlank
        String externalId,

        @NotBlank
        String name,

        @NotBlank
        String setId,

        @NotBlank
        String setName,

        @NotBlank
        String number,

        @NotBlank
        String rarity,

        String imageSmallUrl,

        String imageLargeUrl,

        String description
) {
}
