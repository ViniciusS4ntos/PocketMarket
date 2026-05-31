package com.pocketmarket.listing.dto.response;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ListingResponse(
        UUID id,
        UUID userCardId,
        UUID sellerId,
        String sellerName,
        ListingType listingType,
        ListingStatus listingStatus,
        Long price,
        Long startingBid,
        Long currentBid,
        Long minBidIncrement,
        LocalDateTime auctionEndsAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
