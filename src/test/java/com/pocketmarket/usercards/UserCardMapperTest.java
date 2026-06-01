package com.pocketmarket.usercards;

import com.pocketmarket.cards.Card;
import com.pocketmarket.enums.CardCondition;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserCardMapperTest {

    @Test
    void toEntityMapsRequestUserAndCard() {
        assertThat(new UserCardMapper()).isNotNull();
        User user = User.builder().id(UUID.randomUUID()).build();
        Card card = Card.builder().id(UUID.randomUUID()).build();
        UserCardRequest request = new UserCardRequest("base1-4", CardCondition.LP, "proof.png");

        UserCard userCard = UserCardMapper.toEntity(request, user, card);

        assertThat(userCard.getOwner()).isSameAs(user);
        assertThat(userCard.getCard()).isSameAs(card);
        assertThat(userCard.getCondition()).isEqualTo(request.condition());
        assertThat(userCard.getProofImageUrl()).isEqualTo(request.proofImageUrl());
    }

    @Test
    void toResponseMapsEntity() {
        UUID ownerId = UUID.randomUUID();
        UserCard userCard = UserCard.builder()
                .id(UUID.randomUUID())
                .card(Card.builder().build())
                .owner(User.builder().id(ownerId).name("Ash").build())
                .condition(CardCondition.NM)
                .status(UserCardStatus.AVAILABLE)
                .proofImageUrl("proof.png")
                .acquiredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserCardResponse response = UserCardMapper.toResponse(userCard);

        assertThat(response.id()).isEqualTo(userCard.getId());
        assertThat(response.ownerId()).isEqualTo(ownerId);
        assertThat(response.ownerName()).isEqualTo("Ash");
        assertThat(response.proofImageUrl()).isEqualTo(userCard.getProofImageUrl());
    }
}
