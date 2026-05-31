package com.pocketmarket.auction.service;

import com.pocketmarket.listing.Listing;
import org.springframework.stereotype.Component;

@Component
public class CalculateMinimumBidService {

    public Long calculate(Listing listing) {
        boolean hasCurrentBidder = listing.getCurrentBidder() != null;

        if (!hasCurrentBidder) {
            return listing.getStartingBid();
        }

        return listing.getCurrentBid() + listing.getMinBidIncrement();
    }
}
