package com.pocketmarket.usercards;

import com.pocketmarket.cards.Card;
import com.pocketmarket.cards.CardService;
import com.pocketmarket.enums.CardCondition;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCardServiceTest {

    @Mock
    private UserCardRepository userCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private UserCardService userCardService;

    @Test
    void createUserCardImportsCardAndSavesUserCard() {
        User user = User.builder().id(UUID.randomUUID()).name("Ash").email("ash@pm.com").build();
        Card card = Card.builder().id(UUID.randomUUID()).externalId("base1-4").name("Charizard").build();
        UserCardRequest request = new UserCardRequest("base1-4", CardCondition.NM, "proof.png");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cardService.findOrImportByExternalId(request.externalCardId())).thenReturn(card);
        when(userCardRepository.save(org.mockito.ArgumentMatchers.any(UserCard.class)))
                .thenAnswer(invocation -> {
                    UserCard userCard = invocation.getArgument(0);
                    userCard.setId(UUID.randomUUID());
                    return userCard;
                });

        UserCardResponse response = userCardService.createUserCard(user, request);

        assertThat(response.card()).isSameAs(card);
        assertThat(response.ownerId()).isEqualTo(user.getId());
        assertThat(response.ownerName()).isEqualTo(user.getName());
        assertThat(response.condition()).isEqualTo(CardCondition.NM);
        assertThat(response.status()).isEqualTo(UserCardStatus.AVAILABLE);
        verify(userCardRepository).save(org.mockito.ArgumentMatchers.any(UserCard.class));
    }

    @Test
    void createUserCardThrowsWhenUserDoesNotExist() {
        User user = User.builder().id(UUID.randomUUID()).build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCardService.createUserCard(user, new UserCardRequest("id", CardCondition.NM, "proof")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void getMyCardsReturnsCurrentUserCards() {
        User user = User.builder().id(UUID.randomUUID()).name("Ash").build();
        Card card = Card.builder().id(UUID.randomUUID()).externalId("base1-4").name("Charizard").build();
        UserCard userCard = UserCard.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .card(card)
                .condition(CardCondition.NM)
                .status(UserCardStatus.AVAILABLE)
                .proofImageUrl("proof.png")
                .build();
        PageRequest pageable = PageRequest.of(0, 20);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userCardRepository.findByOwner(user, pageable)).thenReturn(new PageImpl<>(List.of(userCard), pageable, 1));

        var response = userCardService.getMyCards(user, pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().id()).isEqualTo(userCard.getId());
        assertThat(response.getContent().getFirst().ownerId()).isEqualTo(user.getId());
        assertThat(response.getContent().getFirst().card()).isSameAs(card);
    }

    @Test
    void getMyCardsThrowsWhenUserDoesNotExist() {
        User user = User.builder().id(UUID.randomUUID()).build();
        PageRequest pageable = PageRequest.of(0, 20);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCardService.getMyCards(user, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void getUserCardReturnsCardDetails() {
        User user = User.builder().id(UUID.randomUUID()).name("Ash").build();
        Card card = Card.builder().id(UUID.randomUUID()).externalId("base1-4").name("Charizard").build();
        UserCard userCard = UserCard.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .card(card)
                .condition(CardCondition.LP)
                .status(UserCardStatus.AVAILABLE)
                .proofImageUrl("proof.png")
                .build();

        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));

        UserCardResponse response = userCardService.getUserCard(userCard.getId());

        assertThat(response.id()).isEqualTo(userCard.getId());
        assertThat(response.card()).isSameAs(card);
        assertThat(response.ownerName()).isEqualTo(user.getName());
        assertThat(response.condition()).isEqualTo(CardCondition.LP);
    }

    @Test
    void getUserCardThrowsWhenCardDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userCardRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCardService.getUserCard(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("UserCard não encontrado");
    }

    @Test
    void deleteUserCardDeletesWhenCurrentUserOwnsCard() {
        User user = User.builder().id(UUID.randomUUID()).build();
        UserCard userCard = UserCard.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .build();

        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));

        userCardService.deleteUserCard(userCard.getId(), user);

        verify(userCardRepository).delete(userCard);
    }

    @Test
    void deleteUserCardThrowsWhenCardDoesNotExist() {
        User user = User.builder().id(UUID.randomUUID()).build();
        UUID id = UUID.randomUUID();
        when(userCardRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCardService.deleteUserCard(id, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("UserCard não encontrado");
    }

    @Test
    void deleteUserCardThrowsWhenCurrentUserDoesNotOwnCard() {
        User owner = User.builder().id(UUID.randomUUID()).build();
        User currentUser = User.builder().id(UUID.randomUUID()).build();
        UserCard userCard = UserCard.builder()
                .id(UUID.randomUUID())
                .owner(owner)
                .build();

        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));

        assertThatThrownBy(() -> userCardService.deleteUserCard(userCard.getId(), currentUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Você não tem permissão para deletar este UserCard");
    }
}
