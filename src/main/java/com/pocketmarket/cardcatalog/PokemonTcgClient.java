package com.pocketmarket.cardcatalog;

import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSearchResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSingleCardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PokemonTcgClient {

    private final RestClient pokemonTcgRestClient;

    public List<PokemonTcgCardResponse> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }

        PokemonTcgSearchResponse response = pokemonTcgRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cards")
                        .queryParam("q", "name:" + name.trim())
                        .queryParam("pageSize", 10)
                        .build())
                .retrieve()
                .body(PokemonTcgSearchResponse.class);

        return response == null || response.data() == null ? List.of() : response.data();
    }

    public PokemonTcgSearchResponse findCards(Pageable pageable) {
        return pokemonTcgRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cards")
                        .queryParam("page", pageable.getPageNumber() + 1)
                        .queryParam("pageSize", pageable.getPageSize())
                        .queryParamIfPresent("orderBy", toOrderBy(pageable))
                .build())
                .retrieve()
                .body(PokemonTcgSearchResponse.class);
    }

    public PokemonTcgCardResponse findByExternalId(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return null;
        }

        try {
            PokemonTcgSingleCardResponse response = pokemonTcgRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cards/{externalId}")
                            .build(externalId))
                    .retrieve()
                    .body(PokemonTcgSingleCardResponse.class);

            return response == null ? null : response.data();
        } catch (HttpClientErrorException.NotFound exception) {
            return null;
        }
    }

    private Optional<String> toOrderBy(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return Optional.empty();
        }

        String orderBy = pageable.getSort()
                .stream()
                .map(order -> (order.isDescending() ? "-" : "") + order.getProperty())
                .reduce((left, right) -> left + "," + right)
                .orElse("");

        return orderBy.isBlank() ? Optional.empty() : Optional.of(orderBy);
    }
}
