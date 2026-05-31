package com.pocketmarket.auction.dto.response;

import com.pocketmarket.enums.AuctionBidStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionBidResponse(
        UUID id,
        UUID listingId,
        UUID bidderId,
        String bidderName,
        Long amount,
        AuctionBidStatus status,
        LocalDateTime createdAt
) {
}
