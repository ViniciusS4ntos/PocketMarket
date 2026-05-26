package com.pocketmarket.cards;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card create(Card card) {
        return cardRepository.save(card);
    }

    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    public Card findById(UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carta não encontrada"));
    }

    public Card update(UUID id, Card updatedCard) {

        Card existingCard = findById(id);

        existingCard.setName(updatedCard.getName());
        existingCard.setSetName(updatedCard.getSetName());
        existingCard.setRarity(updatedCard.getRarity());
        existingCard.setCondition(updatedCard.getCondition());
        existingCard.setPrice(updatedCard.getPrice());
        existingCard.setStock(updatedCard.getStock());
        existingCard.setImageUrl(updatedCard.getImageUrl());
        existingCard.setDescription(updatedCard.getDescription());

        return cardRepository.save(existingCard);
    }

    public void delete(UUID id) {

        Card card = findById(id);

        card.setDeleted(true);

        cardRepository.save(card);
    }
}