package com.pocketmarket.trade.dto;

import java.util.UUID;

public record TradeOfferResponse(

        UUID id,
        String sender,
        String receiver,
        String status

) {
}