package com.pocketmarket.cards;

import com.pocketmarket.cards.dto.CardRequestDTO;
import com.pocketmarket.cards.dto.CardResponseDTO;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public CardResponseDTO create(@RequestBody CardRequestDTO request) {

        Card card = new Card();

        card.setName(request.name());
        card.setSetName(request.setName());
        card.setRarity(request.rarity());
        card.setCondition(request.condition());
        card.setPrice(request.price());
        card.setStock(request.stock());
        card.setImageUrl(request.imageUrl());
        card.setDescription(request.description());

        Card savedCard = cardService.create(card);

        return new CardResponseDTO(
                savedCard.getId(),
                savedCard.getName(),
                savedCard.getSetName(),
                savedCard.getRarity().name(),
                savedCard.getCondition().name(),
                savedCard.getPrice(),
                savedCard.getStock(),
                savedCard.getImageUrl(),
                savedCard.getDescription()
        );
    }

    @GetMapping
    public List<CardResponseDTO> findAll() {

        return cardService.findAll()
                .stream()
                .map(card -> new CardResponseDTO(
                        card.getId(),
                        card.getName(),
                        card.getSetName(),
                        card.getRarity().name(),
                        card.getCondition().name(),
                        card.getPrice(),
                        card.getStock(),
                        card.getImageUrl(),
                        card.getDescription()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public CardResponseDTO findById(@PathVariable UUID id) {

        Card card = cardService.findById(id);

        return new CardResponseDTO(
                card.getId(),
                card.getName(),
                card.getSetName(),
                card.getRarity().name(),
                card.getCondition().name(),
                card.getPrice(),
                card.getStock(),
                card.getImageUrl(),
                card.getDescription()
        );
    }

    @PatchMapping("/{id}")
    public CardResponseDTO update(
            @PathVariable UUID id,
            @RequestBody CardRequestDTO request
    ) {

        Card card = new Card();

        card.setName(request.name());
        card.setSetName(request.setName());
        card.setRarity(request.rarity());
        card.setCondition(request.condition());
        card.setPrice(request.price());
        card.setStock(request.stock());
        card.setImageUrl(request.imageUrl());
        card.setDescription(request.description());

        Card updatedCard = cardService.update(id, card);

        return new CardResponseDTO(
                updatedCard.getId(),
                updatedCard.getName(),
                updatedCard.getSetName(),
                updatedCard.getRarity().name(),
                updatedCard.getCondition().name(),
                updatedCard.getPrice(),
                updatedCard.getStock(),
                updatedCard.getImageUrl(),
                updatedCard.getDescription()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        cardService.delete(id);
    }
}