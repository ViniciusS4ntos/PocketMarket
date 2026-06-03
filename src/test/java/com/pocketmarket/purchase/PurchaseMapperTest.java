package com.pocketmarket.purchase;

import com.pocketmarket.auction.AuctionBid;
import com.pocketmarket.enums.PurchaseStatus;
import com.pocketmarket.enums.PurchaseType;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCard;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseMapperTest {

    @Test
    void toAuctionPurchaseMapsFields() {
        assertThat(new PurchaseMapper()).isNotNull();
        User seller = user();
        User bidder = user();
        UserCard userCard = UserCard.builder().id(UUID.randomUUID()).owner(seller).build();
        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard)
                .build();
        AuctionBid winningBid = AuctionBid.builder()
                .id(UUID.randomUUID())
                .listing(listing)
                .bidder(bidder)
                .amount(500L)
                .build();

        Purchase purchase = PurchaseMapper.toAuctionPurchase(listing, winningBid, 50L, 450L);

        assertThat(purchase.getListing()).isSameAs(listing);
        assertThat(purchase.getUserCard()).isSameAs(userCard);
        assertThat(purchase.getBuyer()).isSameAs(bidder);
        assertThat(purchase.getSeller()).isSameAs(seller);
        assertThat(purchase.getType()).isEqualTo(PurchaseType.AUCTION);
        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.COMPLETED);
        assertThat(purchase.getAmount()).isEqualTo(500L);
        assertThat(purchase.getPlatformFee()).isEqualTo(50L);
        assertThat(purchase.getSellerAmount()).isEqualTo(450L);
    }

    @Test
    void toSalePurchaseAndResponseMapFields() {
        User seller = user();
        User buyer = user();
        UserCard userCard = UserCard.builder().id(UUID.randomUUID()).owner(seller).build();
        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard)
                .listingType(ListingType.SALE)
                .price(300L)
                .build();

        Purchase purchase = PurchaseMapper.toSalePurchase(listing, buyer, 270L, 30L);
        purchase.setId(UUID.randomUUID());
        purchase.setStatus(PurchaseStatus.COMPLETED);
        purchase.setCreatedAt(LocalDateTime.now());

        var response = PurchaseMapper.toResponse(purchase);

        assertThat(purchase.getType()).isEqualTo(PurchaseType.SALE);
        assertThat(purchase.getAmount()).isEqualTo(300L);
        assertThat(response.id()).isEqualTo(purchase.getId());
        assertThat(response.listingId()).isEqualTo(listing.getId());
        assertThat(response.userCardId()).isEqualTo(userCard.getId());
        assertThat(response.buyerId()).isEqualTo(buyer.getId());
        assertThat(response.sellerId()).isEqualTo(seller.getId());
        assertThat(response.platformFee()).isEqualTo(30L);
    }

    private User user() {
        return User.builder().id(UUID.randomUUID()).name("Ash").build();
    }
}
