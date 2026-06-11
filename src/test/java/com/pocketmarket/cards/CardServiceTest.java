package com.pocketmarket.cards;

import com.pocketmarket.cardcatalog.PokemonTcgClient;
import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgImagesResponse;
import com.pocketmarket.cardcatalog.dto.PokemonTcgSetResponse;
import com.pocketmarket.cards.dto.CardResponseDTO;
import com.pocketmarket.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private PokemonTcgClient pokemonTcgClient;

    @InjectMocks
    private CardService cardService;

    @Test
    void findAllCardsMapsCards() {
        Card card = card();
        when(cardRepository.findAll()).thenReturn(List.of(card));

        List<CardResponseDTO> response = cardService.findAllCards();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().externalId()).isEqualTo(card.getExternalId());
    }

    @Test
    void findCardThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.findCard(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Carta não encontrada");
    }

    @Test
    void findCardMapsCardWhenFound() {
        Card card = card();
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        CardResponseDTO response = cardService.findCard(card.getId());

        assertThat(response.id()).isEqualTo(card.getId());
    }

    @Test
    void deleteMarksCardAsDeleted() {
        UUID id = UUID.randomUUID();
        Card card = card();
        when(cardRepository.findById(id)).thenReturn(Optional.of(card));

        cardService.delete(id);

        assertThat(card.getDeleted()).isTrue();
        verify(cardRepository).save(card);
    }

    @Test
    void deleteThrowsWhenCardDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.delete(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Carta não encontrada");
    }

    @Test
    void findOrImportByExternalIdReusesExistingCard() {
        Card card = card();
        when(cardRepository.findByExternalId(card.getExternalId())).thenReturn(Optional.of(card));

        Card response = cardService.findOrImportByExternalId(card.getExternalId());

        assertThat(response).isSameAs(card);
    }

    @Test
    void findOrImportByExternalIdImportsWhenMissing() {
        PokemonTcgCardResponse pokemonCard = new PokemonTcgCardResponse(
                "base1-4",
                "Charizard",
                new PokemonTcgSetResponse("base1", "Base"),
                "4",
                "Rare Holo",
                new PokemonTcgImagesResponse("small", "large")
        );
        when(cardRepository.findByExternalId(pokemonCard.id())).thenReturn(Optional.empty());
        when(pokemonTcgClient.findByExternalId(pokemonCard.id())).thenReturn(pokemonCard);
        when(cardRepository.save(org.mockito.ArgumentMatchers.any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card response = cardService.findOrImportByExternalId(pokemonCard.id());

        assertThat(response.getExternalId()).isEqualTo(pokemonCard.id());
        assertThat(response.getSetName()).isEqualTo("Base");
        verify(cardRepository).save(org.mockito.ArgumentMatchers.any(Card.class));
    }

    @Test
    void findOrImportByExternalIdThrowsWhenApiDoesNotFindCard() {
        when(cardRepository.findByExternalId("missing")).thenReturn(Optional.empty());
        when(pokemonTcgClient.findByExternalId("missing")).thenReturn(null);

        assertThatThrownBy(() -> cardService.findOrImportByExternalId("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Carta não encontrada no Pokémon TCG API.");
    }

    private Card card() {
        return Card.builder()
                .id(UUID.randomUUID())
                .externalId("base1-4")
                .name("Charizard")
                .setId("base1")
                .setName("Base")
                .number("4")
                .rarity("Rare Holo")
                .imageSmallUrl("small")
                .imageLargeUrl("large")
                .build();
    }
}
