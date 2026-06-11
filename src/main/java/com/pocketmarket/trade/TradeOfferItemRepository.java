package com.pocketmarket.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TradeOfferItemRepository
        extends JpaRepository<TradeOfferItem, UUID> {

    List<TradeOfferItem> findByTradeOffer(TradeOffer tradeOffer);

    @Query("""
            SELECT item
            FROM TradeOfferItem item
            JOIN FETCH item.userCard userCard
            JOIN FETCH userCard.owner
            WHERE item.tradeOffer.id = :tradeOfferId
            """)
    List<TradeOfferItem> findByTradeOfferIdWithCards(UUID tradeOfferId);
}
