package com.pocketmarket.collection;

import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCard;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "collection_cards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "user_card_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CollectionCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_card_id", nullable = false)
    private UserCard userCard;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    private void prePersist() {
        this.addedAt = LocalDateTime.now();
    }
}