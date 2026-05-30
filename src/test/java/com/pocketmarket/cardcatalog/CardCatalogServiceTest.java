package com.pocketmarket.cardcatalog;

import com.pocketmarket.cardcatalog.dto.CardCatalogResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgImagesResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSearchResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSetResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardCatalogServiceTest {

    @Mock
    private PokemonTcgClient pokemonTcgClient;

    @InjectMocks
    private CardCatalogService cardCatalogService;

    @Test
    void searchByNameMapsClientResponse() {
        PokemonTcgCardResponse card = pokemonCard();
        when(pokemonTcgClient.searchByName("charizard")).thenReturn(List.of(card));

        List<CardCatalogResponse> response = cardCatalogService.searchByName("charizard");

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().externalCardId()).isEqualTo(card.id());
        assertThat(response.getFirst().setName()).isEqualTo(card.set().name());
    }

    @Test
    void findCardsReturnsEmptyPageWhenClientReturnsNull() {
        Page<CardCatalogResponse> response = cardCatalogService.findCards(PageRequest.of(0, 20));

        assertThat(response).isEmpty();
    }

    @Test
    void findCardsMapsPageData() {
        when(pokemonTcgClient.findCards(PageRequest.of(0, 20)))
                .thenReturn(new PokemonTcgSearchResponse(List.of(pokemonCard()), 1, 20, 1, 1));

        Page<CardCatalogResponse> response = cardCatalogService.findCards(PageRequest.of(0, 20));

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().name()).isEqualTo("Charizard");
    }

    @Test
    void findByExternalIdThrowsWhenCardDoesNotExist() {
        when(pokemonTcgClient.findByExternalId("missing")).thenReturn(null);

        assertThatThrownBy(() -> cardCatalogService.findByExternalId("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Carta não encontrada no catálogo");
    }

    @Test
    void findByExternalIdMapsCard() {
        when(pokemonTcgClient.findByExternalId("base1-4")).thenReturn(pokemonCard());

        CardCatalogResponse response = cardCatalogService.findByExternalId("base1-4");

        assertThat(response.externalCardId()).isEqualTo("base1-4");
        assertThat(response.imageLargeUrl()).isEqualTo("large.png");
    }

    private PokemonTcgCardResponse pokemonCard() {
        return new PokemonTcgCardResponse(
                "base1-4",
                "Charizard",
                new PokemonTcgSetResponse("base1", "Base"),
                "4",
                "Rare Holo",
                new PokemonTcgImagesResponse("small.png", "large.png")
        );
    }
}
