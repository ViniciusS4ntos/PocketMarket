package com.pocketmarket.listing.dto.request;

import com.pocketmarket.cards.Card;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.user.User;

import java.time.LocalDateTime;

public record ListingRequest(
        Card card,
        User seller,
        ListingType listingType,
        Long price,
        Long startingBid,
        Long currentBid,
        Long minBidIncrement,
        LocalDateTime auctionEndsAt
) {
}
