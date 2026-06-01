package com.pocketmarket.auction.dto.response;

import com.pocketmarket.enums.ListingStatus;

import java.util.UUID;

public record AuctionFinishResponse(
        UUID listingId,
        ListingStatus listingStatus,
        UUID purchaseId,
        UUID winnerId,
        Long amount,
        Long sellerAmount,
        Long platformFee,
        String message
) {
}
