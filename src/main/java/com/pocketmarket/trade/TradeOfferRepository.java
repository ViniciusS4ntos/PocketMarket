package com.pocketmarket.trade;

import com.pocketmarket.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TradeOfferRepository
        extends JpaRepository<TradeOffer, UUID> {

    List<TradeOffer> findBySender(User sender);

    List<TradeOffer> findByReceiver(User receiver);
}