package com.pocketmarket.auction.mapper;

import com.pocketmarket.auction.AuctionBid;
import com.pocketmarket.auction.dto.request.AuctionBidRequest;
import com.pocketmarket.auction.dto.response.AuctionBidResponse;
import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.user.User;

public class AuctionBidMapper {

    public static AuctionBid toEntity(User user, Listing listing, AuctionBidRequest request) {
        return AuctionBid.builder()
                .listing(listing)
                .bidder(user)
                .amount(request.amount())
                .status(AuctionBidStatus.WINNING)
                .build();
    }

    public static AuctionBidResponse toResponse(AuctionBid auctionBid) {
        return new AuctionBidResponse(
                auctionBid.getId(),
                auctionBid.getListing().getId(),
                auctionBid.getBidder().getId(),
                auctionBid.getBidder().getName(),
                auctionBid.getAmount(),
                auctionBid.getStatus(),
                auctionBid.getCreatedAt()
        );
    }
}
