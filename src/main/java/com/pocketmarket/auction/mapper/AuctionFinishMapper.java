package com.pocketmarket.auction.mapper;

import com.pocketmarket.auction.dto.response.AuctionFinishResponse;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.purchase.Purchase;

public class AuctionFinishMapper {
    public static AuctionFinishResponse toResponse(Purchase savedPurchase) {
        return new AuctionFinishResponse(
                savedPurchase.getListing().getId(),
                savedPurchase.getListing().getListingStatus(),
                savedPurchase.getId(),
                savedPurchase.getBuyer().getId(),
                savedPurchase.getAmount(),
                savedPurchase.getSellerAmount(),
                savedPurchase.getPlatformFee(),
                "Leilão finalizado com vencedor"
        );
    }

    public static AuctionFinishResponse toResponseWithoutWinner(Listing listing) {
        return new AuctionFinishResponse(
                listing.getId(),
                listing.getListingStatus(),
                null,
                null,
                null,
                null,
                null,
                "Leilão expirado sem lance"
        );
    }
}
