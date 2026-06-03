package com.pocketmarket.collection;

import com.pocketmarket.collection.dto.CollectionRequest;
import com.pocketmarket.collection.dto.CollectionResponse;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
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
    private UserCardRepository userCardRepository;

    @InjectMocks
    private CollectionService collectionService;

    @Test
    void addToCollectionCreatesNewItem() {
        User user = user();
        UserCard userCard = userCard(user);
        CollectionRequest request = new CollectionRequest(userCard.getId());

        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));
        when(collectionRepository.findByUserIdAndUserCardId(user.getId(), userCard.getId())).thenReturn(Optional.empty());
        when(collectionRepository.save(org.mockito.ArgumentMatchers.any(CollectionCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CollectionResponse response = collectionService.addToCollection(request, user);

        assertThat(response.userCardId()).isEqualTo(userCard.getId());
        verify(collectionRepository).save(org.mockito.ArgumentMatchers.any(CollectionCard.class));
    }

    @Test
    void addToCollectionReturnsExistingItemWhenAlreadyInCollection() {
        User user = user();
        UserCard userCard = userCard(user);
        CollectionCard existing = CollectionCard.builder()
                .user(user)
                .userCard(userCard)
                .addedAt(LocalDateTime.now())
                .build();

        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));
        when(collectionRepository.findByUserIdAndUserCardId(user.getId(), userCard.getId())).thenReturn(Optional.of(existing));

        CollectionResponse response = collectionService.addToCollection(new CollectionRequest(userCard.getId()), user);

        assertThat(response.userCardId()).isEqualTo(userCard.getId());
        verify(collectionRepository).save(existing);
    }

    @Test
    void addToCollectionThrowsWhenCardDoesNotExist() {
        UUID userCardId = UUID.randomUUID();
        when(userCardRepository.findById(userCardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collectionService.addToCollection(new CollectionRequest(userCardId), user()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Card not found");
    }

    @Test
    void addToCollectionThrowsWhenUserCardBelongsToAnotherUser() {
        User user = user();
        User otherUser = user();
        UserCard userCard = userCard(otherUser);

        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));

        assertThatThrownBy(() -> collectionService.addToCollection(new CollectionRequest(userCard.getId()), user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UserCard não pertence ao usuario atual");
    }

    @Test
    void listCollectionMapsItems() {
        User user = user();
        UserCard userCard = userCard(user);
        CollectionCard item = CollectionCard.builder()
                .id(UUID.randomUUID())
                .user(user)
                .userCard(userCard)
                .addedAt(LocalDateTime.now())
                .build();
        when(collectionRepository.findAllByUserId(user.getId())).thenReturn(List.of(item));

        List<CollectionResponse> response = collectionService.listCollection(user);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().collectionId()).isEqualTo(item.getId());
        assertThat(response.getFirst().userCardId()).isEqualTo(userCard.getId());
    }

    @Test
    void removeFromCollectionDeletesItem() {
        User user = user();
        UserCard userCard = userCard(user);
        CollectionCard item = CollectionCard.builder().user(user).userCard(userCard).build();
        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));
        when(collectionRepository.findByUserIdAndUserCardId(user.getId(), userCard.getId())).thenReturn(Optional.of(item));

        collectionService.removeFromCollection(userCard.getId(), user);

        verify(collectionRepository).delete(item);
    }

    @Test
    void removeFromCollectionThrowsWhenItemDoesNotExist() {
        User user = user();
        UserCard userCard = userCard(user);
        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));
        when(collectionRepository.findByUserIdAndUserCardId(user.getId(), userCard.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collectionService.removeFromCollection(userCard.getId(), user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UserCard não está na coleção");
    }

    @Test
    void removeFromCollectionThrowsWhenUserCardDoesNotExist() {
        User user = user();
        UUID userCardId = UUID.randomUUID();
        when(userCardRepository.findById(userCardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collectionService.removeFromCollection(userCardId, user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Card not found");
    }

    @Test
    void removeFromCollectionThrowsWhenUserCardBelongsToAnotherUser() {
        User user = user();
        User otherUser = user();
        UserCard userCard = userCard(otherUser);

        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));

        assertThatThrownBy(() -> collectionService.removeFromCollection(userCard.getId(), user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UserCard não pertence ao usuario atual");
    }

    private User user() {
        return User.builder().id(UUID.randomUUID()).email("ash@pm.com").build();
    }

    private UserCard userCard(User owner) {
        return UserCard.builder()
                .id(UUID.randomUUID())
                .owner(owner)
                .build();
    }
}
