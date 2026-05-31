package com.pocketmarket.auction;

import com.pocketmarket.auction.service.CalculateMinimumBidService;
import com.pocketmarket.auction.validator.AuctionBidValidator;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuctionBidValidatorTest {

    private final AuctionBidValidator validator = new AuctionBidValidator(new CalculateMinimumBidService());

    @Test
    void validateAcceptsValidBid() {
        Listing listing = auction();
        User bidder = user(1000L);

        assertThatCode(() -> validator.validate(listing, bidder, 100L)).doesNotThrowAnyException();

        listing.setCurrentBidder(user(1000L));
        assertThatCode(() -> validator.validate(listing, bidder, 110L)).doesNotThrowAnyException();
    }

    @Test
    void validateRejectsInvalidAuctionState() {
        User bidder = user(1000L);

        assertThatThrownBy(() -> validator.validate(Listing.builder().listingType(ListingType.SALE).build(), bidder, 100L))
                .hasMessage("This listing is not an auction.");

        Listing inactive = auction();
        inactive.setListingStatus(ListingStatus.CANCELED);
        assertThatThrownBy(() -> validator.validate(inactive, bidder, 100L))
                .hasMessage("This auction is not active.");

        Listing withoutEndDate = auction();
        withoutEndDate.setAuctionEndsAt(null);
        assertThatThrownBy(() -> validator.validate(withoutEndDate, bidder, 100L))
                .hasMessage("Auction end date is not defined.");

        Listing ended = auction();
        ended.setAuctionEndsAt(LocalDateTime.now().minusMinutes(1));
        assertThatThrownBy(() -> validator.validate(ended, bidder, 100L))
                .hasMessage("This auction has already ended.");
    }

    @Test
    void validateRejectsInvalidBidderOrAmount() {
        Listing listing = auction();
        User seller = listing.getSeller();
        User bidder = user(1000L);

        assertThatThrownBy(() -> validator.validate(listing, seller, 100L))
                .hasMessage("Seller cannot bid on their own auction.");

        assertThatThrownBy(() -> validator.validate(listing, bidder, null))
                .hasMessage("Bid amount is required.");

        assertThatThrownBy(() -> validator.validate(listing, bidder, 0L))
                .hasMessage("Bid amount must be greater than zero.");

        assertThatThrownBy(() -> validator.validate(listing, bidder, 90L))
                .hasMessage("Bid amount must be at least 100.");

        assertThatThrownBy(() -> validator.validate(listing, user(50L), 100L))
                .hasMessage("Insufficient credits to place this bid.");
    }

    private Listing auction() {
        return Listing.builder()
                .id(UUID.randomUUID())
                .seller(user(1000L))
                .listingType(ListingType.AUCTION)
                .listingStatus(ListingStatus.ACTIVE)
                .startingBid(100L)
                .currentBid(100L)
                .minBidIncrement(10L)
                .auctionEndsAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    private User user(Long credits) {
        return User.builder().id(UUID.randomUUID()).name("Misty").credits(credits).build();
    }
}
