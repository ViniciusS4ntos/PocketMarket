package com.pocketmarket.cards;

import com.pocketmarket.cards.dto.CardResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public List<CardResponseDTO> findAllCards() {
        return cardService.findAllCards();
    }

    @GetMapping("/{id}")
    public CardResponseDTO findCard(@PathVariable UUID id) {
        return cardService.findCard(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        cardService.delete(id);
    }
}