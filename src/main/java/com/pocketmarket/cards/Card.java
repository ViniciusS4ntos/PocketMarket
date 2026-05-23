package com.pocketmarket.cards;

import enums.CardCondition;
import enums.CardRarity;
import com.pocketmarket.user.User;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    private String setName;

    @Enumerated(EnumType.STRING)
    private CardRarity rarity;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_condition")
    private CardCondition condition;

    @Column(nullable = false)
    private BigDecimal price;

    private Integer stock;

    private String imageUrl;

    private String description;

    private Boolean deleted = false;
}
