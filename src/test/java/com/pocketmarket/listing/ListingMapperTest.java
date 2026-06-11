package com.pocketmarket.listing;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.listing.dto.request.ListingAuctionRequest;
import com.pocketmarket.listing.dto.request.ListingSaleRequest;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCard;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListingMapperTest {

    @Test
    void toSaleEntityMapsRequest() {
        assertThat(new ListingMapper()).isNotNull();
        User seller = user();
        UserCard userCard = userCard(seller);

        Listing listing = ListingMapper.toSaleEntity(seller, userCard, new ListingSaleRequest(150L));

        assertThat(listing.getSeller()).isSameAs(seller);
        assertThat(listing.getUserCard()).isSameAs(userCard);
        assertThat(listing.getListingType()).isEqualTo(ListingType.SALE);
        assertThat(listing.getPrice()).isEqualTo(150L);
        assertThat(listing.getCreatedAt()).isNotNull();
        assertThat(listing.getUpdatedAt()).isNotNull();
    }

    @Test
    void toAuctionEntityMapsRequest() {
        User seller = user();
        UserCard userCard = userCard(seller);
        LocalDateTime endsAt = LocalDateTime.now().plusDays(1);

        Listing listing = ListingMapper.toAuctionEntity(seller, userCard, new ListingAuctionRequest(100L, 10L, endsAt));

        assertThat(listing.getSeller()).isSameAs(seller);
        assertThat(listing.getUserCard()).isSameAs(userCard);
        assertThat(listing.getListingType()).isEqualTo(ListingType.AUCTION);
        assertThat(listing.getStartingBid()).isEqualTo(100L);
        assertThat(listing.getCurrentBid()).isEqualTo(100L);
        assertThat(listing.getMinBidIncrement()).isEqualTo(10L);
        assertThat(listing.getAuctionEndsAt()).isEqualTo(endsAt);
        assertThat(listing.getCreatedAt()).isNotNull();
        assertThat(listing.getUpdatedAt()).isNotNull();
    }

    @Test
    void mapsListingResponses() {
        User seller = user();
        UserCard userCard = userCard(seller);
        LocalDateTime now = LocalDateTime.now();
        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard)
                .listingType(ListingType.AUCTION)
                .listingStatus(ListingStatus.ACTIVE)
                .startingBid(100L)
                .currentBid(120L)
                .minBidIncrement(10L)
                .auctionEndsAt(now.plusDays(1))
                .createdAt(now)
                .updatedAt(now)
                .build();

        var auctionResponse = ListingMapper.toAuctionResponse(listing);
        var response = ListingMapper.toResponse(listing);

        assertThat(auctionResponse.id()).isEqualTo(listing.getId());
        assertThat(auctionResponse.userCardId()).isEqualTo(userCard.getId());
        assertThat(auctionResponse.sellerId()).isEqualTo(seller.getId());
        assertThat(response.id()).isEqualTo(listing.getId());
        assertThat(response.currentBid()).isEqualTo(120L);
        assertThat(response.listingType()).isEqualTo(ListingType.AUCTION);
    }

    @Test
    void mapsSaleResponse() {
        User seller = user();
        UserCard userCard = userCard(seller);
        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard)
                .listingType(ListingType.SALE)
                .listingStatus(ListingStatus.ACTIVE)
                .price(200L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var response = ListingMapper.toSaleResponse(listing);

        assertThat(response.id()).isEqualTo(listing.getId());
        assertThat(response.userCardId()).isEqualTo(userCard.getId());
        assertThat(response.sellerName()).isEqualTo(seller.getName());
        assertThat(response.price()).isEqualTo(200L);
    }

    private User user() {
        return User.builder().id(UUID.randomUUID()).name("Ash").credits(1000L).build();
    }

    private UserCard userCard(User owner) {
        return UserCard.builder().id(UUID.randomUUID()).owner(owner).build();
    }
}
