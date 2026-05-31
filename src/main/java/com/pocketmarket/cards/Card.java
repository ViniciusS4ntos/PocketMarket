package com.pocketmarket.cards;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String externalId;

    @Column(nullable = false)
    private String name;

    private String setId;

    private String setName;

    private String number;

    private String rarity;

    @Column(length = 500)
    private String imageSmallUrl;

    @Column(length = 500)
    private String imageLargeUrl;

    private String description;

    @Column(nullable = false)
    @Builder.Default
    private String source = "POKEMON_TCG_API";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.source == null || this.source.isBlank()) {
            this.source = "POKEMON_TCG_API";
        }
    }

    @Builder.Default
    private Boolean deleted = false;

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
