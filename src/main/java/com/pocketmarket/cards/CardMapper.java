package com.pocketmarket.cards;

import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgImagesResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSetResponse;
import com.pocketmarket.cards.dto.CardRequestDTO;
import com.pocketmarket.cards.dto.CardResponseDTO;

public class CardMapper {

    public static CardResponseDTO toResponse(Card savedCard) {
        return new CardResponseDTO(
                savedCard.getId(),
                savedCard.getExternalId(),
                savedCard.getName(),
                savedCard.getSetId(),
                savedCard.getSetName(),
                savedCard.getNumber(),
                savedCard.getRarity(),
                savedCard.getImageSmallUrl(),
                savedCard.getImageLargeUrl(),
                savedCard.getDescription(),
                savedCard.getSource(),
                savedCard.getCreatedAt(),
                savedCard.getUpdatedAt()
        );
    }

    public static Card toEntity(PokemonTcgCardResponse response) {
        PokemonTcgSetResponse set = response.set();
        PokemonTcgImagesResponse images = response.images();

        return Card.builder()
                .externalId(response.id())
                .name(response.name())
                .setId(set != null ? set.id() : null)
                .setName(set != null ? set.name() : null)
                .number(response.number())
                .rarity(response.rarity())
                .imageSmallUrl(images != null ? images.small() : null)
                .imageLargeUrl(images != null ? images.large() : null)
                .source("POKEMON_TCG_API")
                .build();
    }
}
