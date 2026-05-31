package com.pocketmarket.auction;

import com.pocketmarket.auction.dto.request.AuctionBidRequest;
import com.pocketmarket.auction.mapper.AuctionBidMapper;
import com.pocketmarket.auction.mapper.AuctionFinishMapper;
import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.purchase.Purchase;
import com.pocketmarket.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionMapperTest {

    @Test
    void mapsAuctionBidEntityAndResponse() {
        assertThat(new AuctionBidMapper()).isNotNull();
        User bidder = user();
        Listing listing = Listing.builder().id(UUID.randomUUID()).build();

        AuctionBid bid = AuctionBidMapper.toEntity(bidder, listing, new AuctionBidRequest(300L));

        assertThat(bid.getBidder()).isSameAs(bidder);
        assertThat(bid.getListing()).isSameAs(listing);
        assertThat(bid.getAmount()).isEqualTo(300L);
        assertThat(bid.getStatus()).isEqualTo(AuctionBidStatus.WINNING);

        bid.setId(UUID.randomUUID());
        bid.setCreatedAt(LocalDateTime.now());
        var response = AuctionBidMapper.toResponse(bid);

        assertThat(response.id()).isEqualTo(bid.getId());
        assertThat(response.listingId()).isEqualTo(listing.getId());
        assertThat(response.bidderName()).isEqualTo(bidder.getName());
    }

    @Test
    void mapsAuctionFinishResponses() {
        assertThat(new AuctionFinishMapper()).isNotNull();
        User buyer = user();
        Listing listing = Listing.builder().id(UUID.randomUUID()).listingStatus(ListingStatus.SOLD).build();
        Purchase purchase = Purchase.builder()
                .id(UUID.randomUUID())
                .listing(listing)
                .buyer(buyer)
                .amount(500L)
                .sellerAmount(450L)
                .platformFee(50L)
                .build();

        var response = AuctionFinishMapper.toResponse(purchase);
        var withoutWinner = AuctionFinishMapper.toResponseWithoutWinner(
                Listing.builder().id(UUID.randomUUID()).listingStatus(ListingStatus.EXPIRED).build()
        );

        assertThat(response.purchaseId()).isEqualTo(purchase.getId());
        assertThat(response.winnerId()).isEqualTo(buyer.getId());
        assertThat(response.message()).isEqualTo("Leilão finalizado com vencedor");
        assertThat(withoutWinner.purchaseId()).isNull();
        assertThat(withoutWinner.message()).isEqualTo("Leilão expirado sem lance");
    }

    private User user() {
        return User.builder().id(UUID.randomUUID()).name("Ash").build();
    }
}
