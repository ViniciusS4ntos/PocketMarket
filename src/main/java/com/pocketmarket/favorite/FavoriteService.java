package com.pocketmarket.favorite;

import com.pocketmarket.cards.Card;
import com.pocketmarket.cards.CardRepository;
import com.pocketmarket.favorite.dto.FavoriteResponse;
import com.pocketmarket.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final CardRepository cardRepository;

    @Transactional
    public FavoriteResponse addFavorite(UUID cardId, User currentUser) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));



        if (favoriteRepository.existsByUserIdAndCardId(currentUser.getId(), cardId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Card already favorited");
        }

        Favorite favorite = Favorite.builder()
                .user(currentUser)
                .card(card)
                .build();

        if (!favorite.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own favorites");
        }

        favoriteRepository.save(favorite);
        return toResponse(favorite);
    }

    public List<FavoriteResponse> listFavorites(User currentUser) {
        return favoriteRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void removeFavorite(UUID cardId, User currentUser) {
        Favorite favorite = favoriteRepository
                .findByUserIdAndCardId(currentUser.getId(), cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));

        favoriteRepository.delete(favorite);
    }

    private FavoriteResponse toResponse(Favorite favorite) {
        Card card = favorite.getCard();
        return new FavoriteResponse(
                favorite.getId(),
                card.getId(),
                card.getName(),
                card.getSetName(),
                card.getRarity(),
                null,
                null,
                card.getImageSmallUrl()
        );
    }
}
