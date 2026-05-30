package com.pocketmarket.cardcatalog;

import com.pocketmarket.cardcatalog.dto.CardCatalogResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardCatalogService {

    private final PokemonTcgClient pokemonTcgClient;

    public List<CardCatalogResponse> searchByName(String name) {

        if (name == null || name.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carta não encontrada no catálogo");
        }

        return pokemonTcgClient.searchByName(name)
                .stream()
                .map(CardCatalogMapper::toResponse)
                .toList();
    }

    public Page<CardCatalogResponse> findCards(Pageable pageable) {
        PokemonTcgSearchResponse response = pokemonTcgClient.findCards(pageable);

        if (response == null) {
            return Page.empty(pageable);
        }

        List<CardCatalogResponse> cards = response.data() == null
                ? List.of()
                : response.data().stream().map(CardCatalogMapper::toResponse).toList();

        return new PageImpl<>(cards, pageable, response.totalCount());
    }

    public CardCatalogResponse findByExternalId(String externalId) {
        var response = pokemonTcgClient.findByExternalId(externalId);

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Carta não encontrada no catálogo");
        }

        return CardCatalogMapper.toResponse(response);
    }
}
