package com.pocketmarket.collection;

import com.pocketmarket.collection.dto.CollectionRequest;
import com.pocketmarket.collection.dto.CollectionResponse;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
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
    private final UserCardRepository userCardRepository;

    public CollectionResponse addToCollection(CollectionRequest request, User currentUser) {
        UserCard userCard = userCardRepository.findById(request.userCardId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        if (!userCard.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UserCard não pertence ao usuario atual");
        }

        Optional<CollectionCard> existing = collectionRepository
                .findByUserIdAndUserCardId(currentUser.getId(), request.userCardId());

        CollectionCard collectionCard;

        if (existing.isPresent()) {
            collectionCard = existing.get();
        } else {
            collectionCard = CollectionCard.builder()
                    .user(currentUser)
                    .userCard(userCard)
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

    public void removeFromCollection(UUID userCardId, User currentUser) {

        UserCard userCard = userCardRepository.findById(userCardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        if (!userCard.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UserCard não pertence ao usuario atual");
        }

        CollectionCard collectionCard = collectionRepository
                .findByUserIdAndUserCardId(currentUser.getId(), userCardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserCard não está na coleção"));

        collectionRepository.delete(collectionCard);
    }

    private CollectionResponse toResponse(CollectionCard cc) {
        UserCard userCard = cc.getUserCard();
        return new CollectionResponse(
                cc.getId(),
                userCard.getId(),
                cc.getAddedAt()
        );
    }
}