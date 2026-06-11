package com.pocketmarket.trade.dto;

import java.util.List;
import java.util.UUID;

public record CreateTradeOfferRequest(

        UUID receiverId,

        List<UUID> offeredCardIds,

        List<UUID> requestedCardIds

) {
}