package com.pocketmarket.auction.validator;

import com.pocketmarket.auction.service.CalculateMinimumBidService;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.exceptions.BusinessException;
import com.pocketmarket.exceptions.ForbiddenException;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuctionBidValidator {

    private final CalculateMinimumBidService calculateMinimumBidService;

    public void validate(Listing listing, User bidder, Long amount) {

        if (listing.getListingType() != ListingType.AUCTION) {
            throw new BusinessException("This listing is not an auction.");
        }

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("This auction is not active.");
        }

        if (listing.getAuctionEndsAt() == null) {
            throw new BusinessException("Auction end date is not defined.");
        }

        if (listing.getAuctionEndsAt().isBefore(LocalDateTime.now())
                || listing.getAuctionEndsAt().isEqual(LocalDateTime.now())) {
            throw new BusinessException("This auction has already ended.");
        }

        if (listing.getSeller().getId().equals(bidder.getId())) {
            throw new ForbiddenException("Seller cannot bid on their own auction.");
        }

        if (amount == null) {
            throw new BusinessException("Bid amount is required.");
        }

        if (amount <= 0) {
            throw new BusinessException("Bid amount must be greater than zero.");
        }

        Long minimumBid = calculateMinimumBidService.calculate(listing);

        if (amount < minimumBid) {
            throw new BusinessException("Bid amount must be at least " + minimumBid + ".");
        }

        if (bidder.getCredits() < amount) {
            throw new BusinessException("Insufficient credits to place this bid.");
        }
    }
}