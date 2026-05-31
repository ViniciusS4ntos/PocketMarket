package com.pocketmarket.listing;

import com.pocketmarket.auction.AuctionBid;
import com.pocketmarket.auction.AuctionBidRepository;
import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.listing.dto.request.ListingAuctionRequest;
import com.pocketmarket.listing.dto.request.ListingSaleRequest;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
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
class ListingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private UserCardRepository userCardRepository;

    @Mock
    private AuctionBidRepository auctionBidRepository;

    @InjectMocks
    private ListingService listingService;

    @Test
    void postListingSaleCreatesSaleAndMarksCardListed() {
        User seller = user();
        UserCard userCard = userCard(seller, UserCardStatus.AVAILABLE);
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));
        when(listingRepository.save(org.mockito.ArgumentMatchers.any(Listing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = listingService.postListingSale(seller, userCard.getId(), new ListingSaleRequest(250L));

        assertThat(response.userCardId()).isEqualTo(userCard.getId());
        assertThat(response.listingType()).isEqualTo(ListingType.SALE);
        assertThat(response.price()).isEqualTo(250L);
        assertThat(userCard.getStatus()).isEqualTo(UserCardStatus.LISTED);
        verify(userCardRepository).save(userCard);
    }

    @Test
    void postListingSaleRejectsCardFromAnotherOwner() {
        User seller = user();
        UserCard userCard = userCard(user(), UserCardStatus.AVAILABLE);
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));

        assertThatThrownBy(() -> listingService.postListingSale(seller, userCard.getId(), new ListingSaleRequest(250L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Voce nao tem permissao para listar este card!");
    }

    @Test
    void postListingSaleRejectsUnavailableCard() {
        User seller = user();
        UserCard userCard = userCard(seller, UserCardStatus.LISTED);
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));

        assertThatThrownBy(() -> listingService.postListingSale(seller, userCard.getId(), new ListingSaleRequest(250L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Este card nao esta disponivel para listagem!");
    }

    @Test
    void postListingSaleThrowsWhenUserOrCardDoesNotExist() {
        User seller = user();
        UUID userCardId = UUID.randomUUID();
        when(userRepository.findById(seller.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.postListingSale(seller, userCardId, new ListingSaleRequest(250L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario nao encontrado!");

        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userCardRepository.findById(userCardId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> listingService.postListingSale(seller, userCardId, new ListingSaleRequest(250L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("UserCard nao encontrado!");
    }

    @Test
    void postListingAuctionCreatesAuctionAndMarksCardListed() {
        User seller = user();
        UserCard userCard = userCard(seller, UserCardStatus.AVAILABLE);
        LocalDateTime endsAt = LocalDateTime.now().plusDays(1);
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));
        when(listingRepository.save(org.mockito.ArgumentMatchers.any(Listing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = listingService.postListingAuction(seller, userCard.getId(), new ListingAuctionRequest(100L, 10L, endsAt));

        assertThat(response.userCardId()).isEqualTo(userCard.getId());
        assertThat(response.listingType()).isEqualTo(ListingType.AUCTION);
        assertThat(response.currentBid()).isEqualTo(100L);
        assertThat(userCard.getStatus()).isEqualTo(UserCardStatus.LISTED);
    }

    @Test
    void postListingAuctionValidatesAuctionFields() {
        User seller = user();
        UserCard userCard = userCard(seller, UserCardStatus.AVAILABLE);
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userCardRepository.findById(userCard.getId())).thenReturn(Optional.of(userCard));

        assertThatThrownBy(() -> listingService.postListingAuction(seller, userCard.getId(), new ListingAuctionRequest(0L, 10L, LocalDateTime.now().plusDays(1))))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("O lance inicial deve ser maior que zero.");

        assertThatThrownBy(() -> listingService.postListingAuction(seller, userCard.getId(), new ListingAuctionRequest(100L, 0L, LocalDateTime.now().plusDays(1))))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("O incremento mínimo deve ser maior que zero.");

        assertThatThrownBy(() -> listingService.postListingAuction(seller, userCard.getId(), new ListingAuctionRequest(100L, 10L, LocalDateTime.now().minusDays(1))))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("A data de fim do leilão deve ser futura.");
    }

    @Test
    void postListingAuctionThrowsWhenUserOrCardDoesNotExist() {
        User seller = user();
        UUID userCardId = UUID.randomUUID();
        ListingAuctionRequest request = new ListingAuctionRequest(100L, 10L, LocalDateTime.now().plusDays(1));
        when(userRepository.findById(seller.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.postListingAuction(seller, userCardId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario nao encontrado!");

        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userCardRepository.findById(userCardId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> listingService.postListingAuction(seller, userCardId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("UserCard nao encontrado!");
    }

    @Test
    void postListingAuctionRejectsCardFromAnotherOwnerOrUnavailableCard() {
        User seller = user();
        ListingAuctionRequest request = new ListingAuctionRequest(100L, 10L, LocalDateTime.now().plusDays(1));
        UserCard otherUserCard = userCard(user(), UserCardStatus.AVAILABLE);
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userCardRepository.findById(otherUserCard.getId())).thenReturn(Optional.of(otherUserCard));

        assertThatThrownBy(() -> listingService.postListingAuction(seller, otherUserCard.getId(), request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Voce nao tem permissao para listar este card!");

        UserCard listedCard = userCard(seller, UserCardStatus.LISTED);
        when(userCardRepository.findById(listedCard.getId())).thenReturn(Optional.of(listedCard));
        assertThatThrownBy(() -> listingService.postListingAuction(seller, listedCard.getId(), request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Este card nao esta disponivel para listagem!");
    }

    @Test
    void showListingsReturnsActiveListings() {
        User seller = user();
        Listing listing = listing(seller, ListingType.SALE, ListingStatus.ACTIVE);
        PageRequest pageable = PageRequest.of(0, 20);
        when(listingRepository.findByListingStatus(ListingStatus.ACTIVE, pageable))
                .thenReturn(new PageImpl<>(List.of(listing), pageable, 1));

        var response = listingService.showListings(pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().id()).isEqualTo(listing.getId());
    }

    @Test
    void showListingReturnsListingOrThrows() {
        Listing listing = listing(user(), ListingType.SALE, ListingStatus.ACTIVE);
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));

        assertThat(listingService.showListing(listing.getId()).id()).isEqualTo(listing.getId());

        UUID missingId = UUID.randomUUID();
        when(listingRepository.findById(missingId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> listingService.showListing(missingId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Listing nao encontrado!");
    }

    @Test
    void cancelSaleListingMarksListingCanceledAndCardAvailable() {
        User seller = user();
        Listing listing = listing(seller, ListingType.SALE, ListingStatus.ACTIVE);
        listing.getUserCard().setStatus(UserCardStatus.LISTED);
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));

        var response = listingService.cancelListing(seller, listing.getId());

        assertThat(response.listingStatus()).isEqualTo(ListingStatus.CANCELED);
        assertThat(listing.getUserCard().getStatus()).isEqualTo(UserCardStatus.AVAILABLE);
        verify(userCardRepository).save(listing.getUserCard());
        verify(listingRepository).save(listing);
    }

    @Test
    void cancelAuctionListingRefundsWinningBidWhenPresent() {
        User seller = user();
        User bidder = user();
        bidder.setCredits(100L);
        Listing listing = listing(seller, ListingType.AUCTION, ListingStatus.ACTIVE);
        listing.setCurrentBidder(bidder);
        AuctionBid winningBid = AuctionBid.builder()
                .id(UUID.randomUUID())
                .listing(listing)
                .bidder(bidder)
                .amount(300L)
                .status(AuctionBidStatus.WINNING)
                .build();

        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(auctionBidRepository.findByListingIdAndStatus(listing.getId(), AuctionBidStatus.WINNING))
                .thenReturn(Optional.of(winningBid));

        listingService.cancelListing(seller, listing.getId());

        assertThat(bidder.getCredits()).isEqualTo(400L);
        assertThat(winningBid.getStatus()).isEqualTo(AuctionBidStatus.CANCELLED);
        assertThat(listing.getCurrentBidder()).isNull();
        verify(userRepository).save(bidder);
        verify(auctionBidRepository).save(winningBid);
    }

    @Test
    void cancelListingValidatesOwnershipAndStatus() {
        User seller = user();
        Listing listing = listing(user(), ListingType.SALE, ListingStatus.ACTIVE);
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));

        assertThatThrownBy(() -> listingService.cancelListing(seller, listing.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Voce nao tem permissao para cancelar este listing!");

        Listing soldListing = listing(seller, ListingType.SALE, ListingStatus.SOLD);
        when(listingRepository.findById(soldListing.getId())).thenReturn(Optional.of(soldListing));
        assertThatThrownBy(() -> listingService.cancelListing(seller, soldListing.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Apenas anúncios ativos podem ser cancelados.");
    }

    @Test
    void cancelListingThrowsWhenUserOrListingDoesNotExist() {
        User seller = user();
        UUID listingId = UUID.randomUUID();
        when(userRepository.findById(seller.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.cancelListing(seller, listingId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario nao encontrado!");

        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> listingService.cancelListing(seller, listingId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Listing nao encontrado!");
    }

    private User user() {
        return User.builder().id(UUID.randomUUID()).name("Ash").credits(1000L).build();
    }

    private UserCard userCard(User owner, UserCardStatus status) {
        return UserCard.builder().id(UUID.randomUUID()).owner(owner).status(status).build();
    }

    private Listing listing(User seller, ListingType type, ListingStatus status) {
        return Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard(seller, UserCardStatus.LISTED))
                .listingType(type)
                .listingStatus(status)
                .price(type == ListingType.SALE ? 250L : null)
                .startingBid(type == ListingType.AUCTION ? 100L : null)
                .currentBid(type == ListingType.AUCTION ? 100L : null)
                .minBidIncrement(type == ListingType.AUCTION ? 10L : null)
                .auctionEndsAt(type == ListingType.AUCTION ? LocalDateTime.now().plusDays(1) : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
