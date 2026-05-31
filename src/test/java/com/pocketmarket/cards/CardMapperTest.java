package com.pocketmarket.cards;

import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgImagesResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSetResponse;
import com.pocketmarket.cards.dto.CardResponseDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CardMapperTest {

    @Test
    void toResponseMapsCard() {
        Card card = Card.builder()
                .id(UUID.randomUUID())
                .externalId("base1-4")
                .name("Charizard")
                .setId("base1")
                .setName("Base")
                .number("4")
                .rarity("Rare Holo")
                .imageSmallUrl("small")
                .imageLargeUrl("large")
                .description("desc")
                .source("POKEMON_TCG_API")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CardResponseDTO response = CardMapper.toResponse(card);

        assertThat(response.id()).isEqualTo(card.getId());
        assertThat(response.externalId()).isEqualTo(card.getExternalId());
        assertThat(response.source()).isEqualTo(card.getSource());
    }

    @Test
    void toEntityMapsPokemonResponseWithNullNestedObjects() {
        PokemonTcgCardResponse response = new PokemonTcgCardResponse("id", "Name", null, "1", "Rare", null);

        Card card = CardMapper.toEntity(response);

        assertThat(card.getSetId()).isNull();
        assertThat(card.getImageSmallUrl()).isNull();
        assertThat(card.getSource()).isEqualTo("POKEMON_TCG_API");
    }

    @Test
    void toEntityMapsPokemonResponse() {
        PokemonTcgCardResponse response = new PokemonTcgCardResponse(
                "id",
                "Name",
                new PokemonTcgSetResponse("set", "Set"),
                "1",
                "Rare",
                new PokemonTcgImagesResponse("small", "large")
        );

        Card card = CardMapper.toEntity(response);

        assertThat(card.getSetName()).isEqualTo("Set");
        assertThat(card.getImageLargeUrl()).isEqualTo("large");
    }
}
