package com.pocketmarket.cardcatalog;

import com.pocketmarket.cardcatalog.dto.CardCatalogResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgImagesResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSetResponse;

public class CardCatalogMapper {

    public static CardCatalogResponse toResponse(PokemonTcgCardResponse card) {
        PokemonTcgSetResponse set = card.set();
        PokemonTcgImagesResponse images = card.images();

        return new CardCatalogResponse(
                card.id(),
                card.name(),
                set != null ? set.id() : null,
                set != null ? set.name() : null,
                card.number(),
                card.rarity(),
                images != null ? images.small() : null,
                images != null ? images.large() : null
        );
    }
}
