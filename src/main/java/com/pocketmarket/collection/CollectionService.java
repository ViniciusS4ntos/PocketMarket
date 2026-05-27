package com.pocketmarket.collection;

import com.pocketmarket.cards.Card;
import com.pocketmarket.cards.CardRepository;
import com.pocketmarket.collection.dto.CollectionRequest;
import com.pocketmarket.collection.dto.CollectionResponse;
import com.pocketmarket.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final CardRepository cardRepository;

    public CollectionResponse addToCollection(CollectionRequest request, User currentUser) {
        Card card = cardRepository.findById(request.cardId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        Optional<CollectionCard> existing = collectionRepository
                .findByUserIdAndCardId(currentUser.getId(), request.cardId());

        CollectionCard collectionCard;

        if (existing.isPresent()) {
            // Upsert: soma a quantity enviada na já existente
            collectionCard = existing.get();
            collectionCard.setQuantity(collectionCard.getQuantity() + request.quantity());
        } else {
            collectionCard = CollectionCard.builder()
                    .user(currentUser)
                    .card(card)
                    .quantity(request.quantity())
                    .build();
        }

        collectionRepository.save(collectionCard);
        return toResponse(collectionCard);
    }

    public List<CollectionResponse> listCollection(User currentUser) {
        return collectionRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void removeFromCollection(UUID cardId, User currentUser) {
        CollectionCard collectionCard = collectionRepository
                .findByUserIdAndCardId(currentUser.getId(), cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not in collection"));

        collectionRepository.delete(collectionCard);
    }

    private CollectionResponse toResponse(CollectionCard cc) {
        Card card = cc.getCard();
        return new CollectionResponse(
                cc.getId(),
                card.getId(),
                card.getName(),
                card.getSetName(),
                card.getRarity() != null ? card.getRarity().name() : null,
                card.getCondition() != null ? card.getCondition().name() : null,
                card.getPrice(),
                card.getImageUrl(),
                cc.getQuantity(),
                cc.getAddedAt()
        );
    }
}