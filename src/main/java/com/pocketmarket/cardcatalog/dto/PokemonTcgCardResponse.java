package com.pocketmarket.cardcatalog.dto;

public record PokemonTcgCardResponse(
        String id,
        String name,
        PokemonTcgSetResponse set,
        String number,
        String rarity,
        PokemonTcgImagesResponse images
) {
}
