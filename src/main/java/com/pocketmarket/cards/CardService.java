package com.pocketmarket.cards;

import com.pocketmarket.cardcatalog.PokemonTcgClient;
import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import com.pocketmarket.cards.dto.CardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final PokemonTcgClient pokemonTcgClient;

    public List<CardResponseDTO> findAllCards() {

        return cardRepository.findAll()
                .stream()
                .map(CardMapper::toResponse)
                .toList();
    }

    public CardResponseDTO findCard(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carta não encontrada"));
        return CardMapper.toResponse(card);
    }

    public void delete(UUID id) {

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carta não encontrada"));

        card.setDeleted(true);

        cardRepository.save(card);
    }

    public Card findOrImportByExternalId(String externalCardId) {
        return cardRepository.findByExternalId(externalCardId)
                .orElseGet(() -> importFromPokemonTcgApi(externalCardId));
    }

    private Card importFromPokemonTcgApi(String externalCardId) {
        PokemonTcgCardResponse pokemonCard = pokemonTcgClient.findByExternalId(externalCardId);

        if (pokemonCard == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Carta não encontrada no Pokémon TCG API.");
        }

        Card card = CardMapper.toEntity(pokemonCard);

        return cardRepository.save(card);
    }
}
