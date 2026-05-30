package com.pocketmarket.usercards;

import com.pocketmarket.cards.Card;
import com.pocketmarket.enums.CardCondition;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_cards")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserCardStatus status = UserCardStatus.AVAILABLE;

    @Column(nullable = false, length = 500)
    private String proofImageUrl;

    @Column(nullable = false)
    private LocalDateTime acquiredAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.acquiredAt = now;
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = UserCardStatus.AVAILABLE;
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
