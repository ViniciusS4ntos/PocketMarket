package com.pocketmarket.collection;

import com.pocketmarket.cards.Card;
import com.pocketmarket.cards.CardRepository;
import com.pocketmarket.collection.dto.CollectionRequest;
import com.pocketmarket.collection.dto.CollectionResponse;
import com.pocketmarket.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CollectionService collectionService;

    @Test
    void addToCollectionCreatesNewItem() {
        User user = user();
        Card card = card();
        CollectionRequest request = new CollectionRequest(card.getId(), 2);

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(collectionRepository.findByUserIdAndCardId(user.getId(), card.getId())).thenReturn(Optional.empty());
        when(collectionRepository.save(org.mockito.ArgumentMatchers.any(CollectionCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CollectionResponse response = collectionService.addToCollection(request, user);

        assertThat(response.cardId()).isEqualTo(card.getId());
        assertThat(response.quantity()).isEqualTo(2);
        verify(collectionRepository).save(org.mockito.ArgumentMatchers.any(CollectionCard.class));
    }

    @Test
    void addToCollectionIncrementsExistingItem() {
        User user = user();
        Card card = card();
        CollectionCard existing = CollectionCard.builder()
                .user(user)
                .card(card)
                .quantity(1)
                .addedAt(LocalDateTime.now())
                .build();

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(collectionRepository.findByUserIdAndCardId(user.getId(), card.getId())).thenReturn(Optional.of(existing));

        CollectionResponse response = collectionService.addToCollection(new CollectionRequest(card.getId(), 3), user);

        assertThat(response.quantity()).isEqualTo(4);
        verify(collectionRepository).save(existing);
    }

    @Test
    void addToCollectionThrowsWhenCardDoesNotExist() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collectionService.addToCollection(new CollectionRequest(cardId, 1), user()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Card not found");
    }

    @Test
    void listCollectionMapsItems() {
        User user = user();
        CollectionCard item = CollectionCard.builder()
                .id(UUID.randomUUID())
                .user(user)
                .card(card())
                .quantity(1)
                .addedAt(LocalDateTime.now())
                .build();
        when(collectionRepository.findAllByUserId(user.getId())).thenReturn(List.of(item));

        List<CollectionResponse> response = collectionService.listCollection(user);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().collectionId()).isEqualTo(item.getId());
    }

    @Test
    void removeFromCollectionDeletesItem() {
        User user = user();
        UUID cardId = UUID.randomUUID();
        CollectionCard item = CollectionCard.builder().build();
        when(collectionRepository.findByUserIdAndCardId(user.getId(), cardId)).thenReturn(Optional.of(item));

        collectionService.removeFromCollection(cardId, user);

        verify(collectionRepository).delete(item);
    }

    @Test
    void removeFromCollectionThrowsWhenItemDoesNotExist() {
        User user = user();
        UUID cardId = UUID.randomUUID();
        when(collectionRepository.findByUserIdAndCardId(user.getId(), cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collectionService.removeFromCollection(cardId, user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Card not in collection");
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
