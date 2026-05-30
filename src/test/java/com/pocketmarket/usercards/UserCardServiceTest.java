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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        User user = User.builder().id(UUID.randomUUID()).email("ash@pm.com").build();
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
        assertThat(response.owner()).isSameAs(user);
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
}
