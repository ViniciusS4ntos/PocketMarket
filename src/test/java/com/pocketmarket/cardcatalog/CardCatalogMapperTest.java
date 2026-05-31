package com.pocketmarket.cardcatalog;

import com.pocketmarket.cardcatalog.dto.CardCatalogResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgImagesResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSetResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardCatalogMapperTest {

    @Test
    void toResponseMapsAllFields() {
        PokemonTcgCardResponse card = new PokemonTcgCardResponse(
                "base1-4",
                "Charizard",
                new PokemonTcgSetResponse("base1", "Base"),
                "4",
                "Rare Holo",
                new PokemonTcgImagesResponse("small", "large")
        );

        CardCatalogResponse response = CardCatalogMapper.toResponse(card);

        assertThat(response.externalCardId()).isEqualTo(card.id());
        assertThat(response.name()).isEqualTo(card.name());
        assertThat(response.setId()).isEqualTo(card.set().id());
        assertThat(response.imageSmallUrl()).isEqualTo(card.images().small());
    }

    @Test
    void toResponseHandlesMissingSetAndImages() {
        PokemonTcgCardResponse card = new PokemonTcgCardResponse("id", "Name", null, "1", null, null);

        CardCatalogResponse response = CardCatalogMapper.toResponse(card);

        assertThat(response.setId()).isNull();
        assertThat(response.setName()).isNull();
        assertThat(response.imageSmallUrl()).isNull();
        assertThat(response.imageLargeUrl()).isNull();
    }
}
