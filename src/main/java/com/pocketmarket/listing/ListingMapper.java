package com.pocketmarket.listing;

import com.pocketmarket.enums.ListingType;
import com.pocketmarket.listing.dto.request.ListingAuctionRequest;
import com.pocketmarket.listing.dto.request.ListingSaleRequest;
import com.pocketmarket.listing.dto.response.ListingAuctionResponse;
import com.pocketmarket.listing.dto.response.ListingResponse;
import com.pocketmarket.listing.dto.response.ListingSaleResponse;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCard;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

public class ListingMapper {


    public static Listing toSaleEntity(User user, UserCard userCard, ListingSaleRequest request) {
        return Listing.builder()
                .userCard(userCard)
                .seller(user)
                .listingType(ListingType.SALE)
                .price(request.price())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static ListingSaleResponse toSaleResponse(Listing listing) {
        return new ListingSaleResponse(
                listing.getId(),
                listing.getUserCard().getId(),
                listing.getSeller().getId(),
                listing.getSeller().getName(),
                listing.getListingType(),
                listing.getListingStatus(),
                listing.getPrice(),
                listing.getCreatedAt(),
                listing.getUpdatedAt()
        );
    }

    public static Listing toAuctionEntity(User user, UserCard userCard, @Valid ListingAuctionRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return Listing.builder()
                .userCard(userCard)
                .seller(user)
                .listingType(ListingType.AUCTION)
                .startingBid(request.startingBid())
                .currentBid(request.startingBid())
                .minBidIncrement(request.minBidIncrement())
                .auctionEndsAt(request.auctionEndsAt())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static ListingAuctionResponse toAuctionResponse(Listing auction) {
        return new ListingAuctionResponse(
                auction.getId(),
                auction.getUserCard().getId(),
                auction.getSeller().getId(),
                auction.getSeller().getName(),
                auction.getListingType(),
                auction.getListingStatus(),
                auction.getStartingBid(),
                auction.getCurrentBid(),
                auction.getMinBidIncrement(),
                auction.getAuctionEndsAt(),
                auction.getCreatedAt(),
                auction.getUpdatedAt()
        );
    }

    public static ListingResponse toResponse(Listing listing) {
        return new ListingResponse(
                listing.getId(),
                listing.getUserCard().getId(),
                listing.getSeller().getId(),
                listing.getSeller().getName(),
                listing.getListingType(),
                listing.getListingStatus(),
                listing.getPrice(),
                listing.getStartingBid(),
                listing.getCurrentBid(),
                listing.getMinBidIncrement(),
                listing.getAuctionEndsAt(),
                listing.getCreatedAt(),
                listing.getUpdatedAt()
        );
    }
}
