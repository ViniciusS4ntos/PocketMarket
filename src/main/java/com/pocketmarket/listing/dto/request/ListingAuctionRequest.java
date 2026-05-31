package com.pocketmarket.listing.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ListingAuctionRequest(
        @NotNull
        Long startingBid,

        @NotNull
        Long minBidIncrement,

        @NotNull
        LocalDateTime auctionEndsAt
) {
}
