package com.pocketmarket.purchase;

import com.pocketmarket.auction.AuctionBid;
import com.pocketmarket.enums.PurchaseStatus;
import com.pocketmarket.enums.PurchaseType;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.purchase.dto.response.PurchaseResponse;
import com.pocketmarket.user.User;
import org.springframework.data.domain.Page;

public class PurchaseMapper {

    public static Purchase toAuctionPurchase(Listing listing, AuctionBid winningBid, Long platformFee, Long sellerAmount) {
        return Purchase.builder()
                .listing(listing)
                .userCard(listing.getUserCard())
                .buyer(winningBid.getBidder())
                .seller(listing.getSeller())
                .type(PurchaseType.AUCTION)
                .status(PurchaseStatus.COMPLETED)
                .amount(winningBid.getAmount())
                .sellerAmount(sellerAmount)
                .platformFee(platformFee)
                .build();
    }

    public static PurchaseResponse toResponse(Purchase purchase) {
        return new PurchaseResponse(
                purchase.getId(),
                purchase.getListing().getId(),
                purchase.getUserCard().getId(),
                purchase.getBuyer().getId(),
                purchase.getBuyer().getName(),
                purchase.getSeller().getId(),
                purchase.getSeller().getName(),
                purchase.getType(),
                purchase.getStatus(),
                purchase.getAmount(),
                purchase.getSellerAmount(),
                purchase.getPlatformFee(),
                purchase.getCreatedAt()
        );
    }

    public static Purchase toSalePurchase(Listing listing, User buyer, Long sellerAmount, Long platformFee) {
        return Purchase.builder()
                .listing(listing)
                .userCard(listing.getUserCard())
                .buyer(buyer)
                .seller(listing.getSeller())
                .type(PurchaseType.SALE)
                .amount(listing.getPrice())
                .sellerAmount(sellerAmount)
                .platformFee(platformFee)
                .build();
    }
}
