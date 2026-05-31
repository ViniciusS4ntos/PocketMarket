package com.pocketmarket.trade;

import com.pocketmarket.enums.TradeItemType;
import com.pocketmarket.usercards.UserCard;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "trade_offer_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeOfferItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trade_offer_id")
    private TradeOffer tradeOffer;

    @ManyToOne
    @JoinColumn(name = "user_card_id")
    private UserCard userCard;

    @Enumerated(EnumType.STRING)
    private TradeItemType type;
}