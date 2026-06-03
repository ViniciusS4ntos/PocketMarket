package com.pocketmarket.trade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TradeOfferItemRepository
        extends JpaRepository<TradeOfferItem, UUID> {
}