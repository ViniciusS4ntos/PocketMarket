package com.pocketmarket.favorite;

import com.pocketmarket.cards.Card;
import com.pocketmarket.cards.CardRepository;
import com.pocketmarket.favorite.dto.FavoriteResponse;
import com.pocketmarket.user.User;
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
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void addFavoriteCreatesFavorite() {
        User user = user();
        Card card = card();
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(favoriteRepository.existsByUserIdAndCardId(user.getId(), card.getId())).thenReturn(false);
        when(favoriteRepository.save(org.mockito.ArgumentMatchers.any(Favorite.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FavoriteResponse response = favoriteService.addFavorite(card.getId(), user);

        assertThat(response.cardId()).isEqualTo(card.getId());
        verify(favoriteRepository).save(org.mockito.ArgumentMatchers.any(Favorite.class));
    }

    @Test
    void addFavoriteThrowsWhenCardDoesNotExist() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.addFavorite(cardId, user()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Card not found");
    }

    @Test
    void addFavoriteRejectsDuplicatedFavorite() {
        User user = user();
        Card card = card();
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(favoriteRepository.existsByUserIdAndCardId(user.getId(), card.getId())).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.addFavorite(card.getId(), user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Card already favorited");
    }

    @Test
    void listFavoritesMapsItems() {
        User user = user();
        Favorite favorite = Favorite.builder()
                .id(UUID.randomUUID())
                .user(user)
                .card(card())
                .build();
        when(favoriteRepository.findAllByUserId(user.getId())).thenReturn(List.of(favorite));

        List<FavoriteResponse> response = favoriteService.listFavorites(user);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().favoriteId()).isEqualTo(favorite.getId());
    }

    @Test
    void removeFavoriteDeletesFavorite() {
        User user = user();
        UUID cardId = UUID.randomUUID();
        Favorite favorite = Favorite.builder().build();
        when(favoriteRepository.findByUserIdAndCardId(user.getId(), cardId)).thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite(cardId, user);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void removeFavoriteThrowsWhenMissing() {
        User user = user();
        UUID cardId = UUID.randomUUID();
        when(favoriteRepository.findByUserIdAndCardId(user.getId(), cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.removeFavorite(cardId, user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Favorite not found");
    }

    private User user() {
        return User.builder().id(UUID.randomUUID()).email("ash@pm.com").build();
    }

    private Card card() {
        return Card.builder()
                .id(UUID.randomUUID())
                .name("Charizard")
                .setName("Base")
                .rarity("Rare Holo")
                .imageSmallUrl("small")
                .build();
    }
}
