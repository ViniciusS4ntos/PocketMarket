package com.pocketmarket.auction;

import com.pocketmarket.auction.dto.request.AuctionBidRequest;
import com.pocketmarket.auction.service.AuctionBidService;
import com.pocketmarket.auction.service.AuctionFinishService;
import com.pocketmarket.auction.validator.AuctionBidValidator;
import com.pocketmarket.auction.validator.AuctionCanBeFinishedValidator;
import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.listing.ListingRepository;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private AuctionBidRepository auctionBidRepository;

    @Mock
    private AuctionBidValidator auctionBidValidator;

    @Mock
    private AuctionFinishService auctionFinishService;

    @Mock
    private AuctionCanBeFinishedValidator auctionCanBeFinishedValidator;

    @InjectMocks
    private AuctionBidService auctionBidService;

    @Test
    void createBidDebitsBidderOutbidsPreviousWinnerAndUpdatesListing() {
        User seller = user(1000L);
        User bidder = user(500L);
        User previousBidder = user(100L);
        Listing listing = auction(seller);
        AuctionBid previousWinningBid = AuctionBid.builder()
                .listing(listing)
                .bidder(previousBidder)
                .amount(120L)
                .status(AuctionBidStatus.WINNING)
                .build();

        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(userRepository.findById(bidder.getId())).thenReturn(Optional.of(bidder));
        when(auctionBidRepository.findByListingIdAndStatus(listing.getId(), AuctionBidStatus.WINNING))
                .thenReturn(Optional.of(previousWinningBid));
        when(auctionBidRepository.save(org.mockito.ArgumentMatchers.any(AuctionBid.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = auctionBidService.createBid(bidder, listing.getId(), new AuctionBidRequest(150L));

        assertThat(response.amount()).isEqualTo(150L);
        assertThat(bidder.getCredits()).isEqualTo(350L);
        assertThat(previousBidder.getCredits()).isEqualTo(220L);
        assertThat(previousWinningBid.getStatus()).isEqualTo(AuctionBidStatus.OUTBID);
        assertThat(listing.getCurrentBid()).isEqualTo(150L);
        assertThat(listing.getCurrentBidder()).isSameAs(bidder);
        verify(userRepository).save(previousBidder);
        verify(listingRepository).save(listing);
    }

    @Test
    void createBidThrowsWhenListingOrUserDoesNotExist() {
        User bidder = user(500L);
        UUID listingId = UUID.randomUUID();
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionBidService.createBid(bidder, listingId, new AuctionBidRequest(150L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Leilao nao encontrado!");

        Listing listing = auction(user(1000L));
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(userRepository.findById(bidder.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionBidService.createBid(bidder, listing.getId(), new AuctionBidRequest(150L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario nao encontrado!");
    }

    @Test
    void getBidsReturnsPageAndRejectsNonAuction() {
        Listing listing = auction(user(1000L));
        AuctionBid bid = AuctionBid.builder()
                .id(UUID.randomUUID())
                .listing(listing)
                .bidder(user(1000L))
                .amount(100L)
                .status(AuctionBidStatus.WINNING)
                .createdAt(LocalDateTime.now())
                .build();
        PageRequest pageable = PageRequest.of(0, 20);
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(auctionBidRepository.findByListing(listing, pageable)).thenReturn(new PageImpl<>(List.of(bid), pageable, 1));

        assertThat(auctionBidService.getBids(listing.getId(), pageable).getContent().getFirst().amount()).isEqualTo(100L);

        Listing sale = auction(user(1000L));
        sale.setListingType(ListingType.SALE);
        when(listingRepository.findById(sale.getId())).thenReturn(Optional.of(sale));
        assertThatThrownBy(() -> auctionBidService.getBids(sale.getId(), pageable))
                .hasMessage("Este anúncio não é um leilão.");
    }

    @Test
    void getBidsThrowsWhenListingDoesNotExist() {
        UUID listingId = UUID.randomUUID();
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionBidService.getBids(listingId, PageRequest.of(0, 20)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Leilao nao encontrado!");
    }

    @Test
    void finishAuctionDelegatesToWinnerOrWithoutWinnerFlow() {
        Listing listing = auction(user(1000L));
        AuctionBid winningBid = AuctionBid.builder().listing(listing).bidder(user(1000L)).amount(200L).build();
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(auctionBidRepository.findByListingIdAndStatus(listing.getId(), AuctionBidStatus.WINNING)).thenReturn(Optional.of(winningBid));

        auctionBidService.finishAuction(listing.getId());

        verify(auctionCanBeFinishedValidator).validate(listing);
        verify(auctionFinishService).withWinner(listing, winningBid);

        Listing listingWithoutBid = auction(user(1000L));
        when(listingRepository.findById(listingWithoutBid.getId())).thenReturn(Optional.of(listingWithoutBid));
        when(auctionBidRepository.findByListingIdAndStatus(listingWithoutBid.getId(), AuctionBidStatus.WINNING)).thenReturn(Optional.empty());

        auctionBidService.finishAuction(listingWithoutBid.getId());

        verify(auctionFinishService).withoutWinner(listingWithoutBid);
    }

    @Test
    void finishAuctionThrowsWhenListingDoesNotExist() {
        UUID listingId = UUID.randomUUID();
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionBidService.finishAuction(listingId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Leilao nao encontrado!");
    }

    @Test
    void findExpiredActiveAuctionIdsDelegatesToRepository() {
        UUID id = UUID.randomUUID();
        when(listingRepository.findExpiredActiveAuctionIds(
                org.mockito.ArgumentMatchers.eq(ListingType.AUCTION),
                org.mockito.ArgumentMatchers.eq(ListingStatus.ACTIVE),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)
        )).thenReturn(List.of(id));

        assertThat(auctionBidService.findExpiredActiveAuctionIds()).containsExactly(id);
    }

    private Listing auction(User seller) {
        return Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .listingType(ListingType.AUCTION)
                .listingStatus(ListingStatus.ACTIVE)
                .startingBid(100L)
                .currentBid(100L)
                .minBidIncrement(10L)
                .auctionEndsAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    private User user(Long credits) {
        return User.builder().id(UUID.randomUUID()).name("Brock").credits(credits).build();
    }
}
