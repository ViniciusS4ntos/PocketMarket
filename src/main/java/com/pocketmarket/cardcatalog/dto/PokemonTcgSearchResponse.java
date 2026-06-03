package com.pocketmarket.cardcatalog.dto;

import java.util.List;

public record PokemonTcgSearchResponse(
        List<PokemonTcgCardResponse> data,
        int page,
        int pageSize,
        int count,
        long totalCount
) {
}
