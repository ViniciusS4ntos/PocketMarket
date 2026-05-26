package com.pocketmarket.cards;

import com.pocketmarket.enums.CardCondition;
import com.pocketmarket.enums.CardRarity;
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

    public Card() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public CardRarity getRarity() {
        return rarity;
    }

    public void setRarity(CardRarity rarity) {
        this.rarity = rarity;
    }

    public CardCondition getCondition() {
        return condition;
    }

    public void setCondition(CardCondition condition) {
        this.condition = condition;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
--