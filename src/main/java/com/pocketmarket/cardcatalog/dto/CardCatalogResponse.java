package com.pocketmarket.cardcatalog.dto;

public record CardCatalogResponse(
        String externalCardId,
        String name,
        String setId,
        String setName,
        String number,
        String rarity,
        String imageSmallUrl,
        String imageLargeUrl
) {
}
