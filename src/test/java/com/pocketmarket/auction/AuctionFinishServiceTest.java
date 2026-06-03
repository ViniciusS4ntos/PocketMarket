package com.pocketmarket.auction;

import com.pocketmarket.auction.service.AuctionFinishService;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.listing.ListingRepository;
import com.pocketmarket.purchase.Purchase;
import com.pocketmarket.purchase.PurchaseRepository;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionFinishServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCardRepository userCardRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private AuctionFinishService auctionFinishService;

    @Test
    void withWinnerTransfersCardCreditsAndCreatesPurchase() {
        User seller = user(100L);
        User bidder = user(500L);
        UserCard userCard = UserCard.builder().id(UUID.randomUUID()).owner(seller).status(UserCardStatus.LISTED).build();
        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard)
                .listingStatus(ListingStatus.ACTIVE)
                .build();
        AuctionBid winningBid = AuctionBid.builder().listing(listing).bidder(bidder).amount(1000L).build();

        when(purchaseRepository.save(org.mockito.ArgumentMatchers.any(Purchase.class)))
                .thenAnswer(invocation -> {
                    Purchase purchase = invocation.getArgument(0);
                    purchase.setId(UUID.randomUUID());
                    return purchase;
                });

        var response = auctionFinishService.withWinner(listing, winningBid);

        assertThat(seller.getCredits()).isEqualTo(1000L);
        assertThat(userCard.getOwner()).isSameAs(bidder);
        assertThat(userCard.getStatus()).isEqualTo(UserCardStatus.AVAILABLE);
        assertThat(listing.getListingStatus()).isEqualTo(ListingStatus.SOLD);
        assertThat(listing.getCurrentBid()).isEqualTo(1000L);
        assertThat(response.platformFee()).isEqualTo(100L);
        assertThat(response.sellerAmount()).isEqualTo(900L);
        verify(userRepository).save(seller);
        verify(userCardRepository).save(userCard);
        verify(listingRepository).save(listing);
    }

    @Test
    void withoutWinnerExpiresListingAndMakesCardAvailable() {
        User seller = user(100L);
        UserCard userCard = UserCard.builder().id(UUID.randomUUID()).owner(seller).status(UserCardStatus.LISTED).build();
        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard)
                .listingStatus(ListingStatus.ACTIVE)
                .currentBidder(user(100L))
                .build();

        var response = auctionFinishService.withoutWinner(listing);

        assertThat(response.listingStatus()).isEqualTo(ListingStatus.EXPIRED);
        assertThat(response.purchaseId()).isNull();
        assertThat(listing.getCurrentBidder()).isNull();
        assertThat(userCard.getStatus()).isEqualTo(UserCardStatus.AVAILABLE);
        verify(userCardRepository).save(userCard);
        verify(listingRepository).save(listing);
    }

    private User user(Long credits) {
        return User.builder().id(UUID.randomUUID()).name("Ash").credits(credits).build();
    }
}
