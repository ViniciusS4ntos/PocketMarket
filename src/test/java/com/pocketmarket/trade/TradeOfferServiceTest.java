package com.pocketmarket.trade;

import com.pocketmarket.auction.AuctionBidRepository;
import com.pocketmarket.auth.AuthService;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.enums.TradeItemType;
import com.pocketmarket.enums.TradeStatus;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.exceptions.ForbiddenException;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.listing.ListingRepository;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeOfferServiceTest {

    @Mock
    private TradeOfferRepository tradeOfferRepository;

    @Mock
    private TradeOfferItemRepository tradeOfferItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCardRepository userCardRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private AuctionBidRepository auctionBidRepository;

    @InjectMocks
    private TradeOfferService tradeOfferService;

    @Test
    void acceptTransfersCardsAndCancelsActiveListing() {
        User sender = user("Ash");
        User receiver = user("Misty");
        UserCard offeredCard = userCard(sender, UserCardStatus.AVAILABLE);
        UserCard requestedCard = userCard(receiver, UserCardStatus.LISTED);
        TradeOffer tradeOffer = tradeOffer(sender, receiver);
        TradeOfferItem offeredItem = item(tradeOffer, offeredCard, TradeItemType.OFFERED);
        TradeOfferItem requestedItem = item(tradeOffer, requestedCard, TradeItemType.REQUESTED);
        Listing listing = listing(receiver, requestedCard);

        when(tradeOfferRepository.findById(tradeOffer.getId())).thenReturn(Optional.of(tradeOffer));
        when(authService.getAuthenticatedUser()).thenReturn(receiver);
        when(tradeOfferItemRepository.findByTradeOfferIdWithCards(tradeOffer.getId())).thenReturn(List.of(offeredItem, requestedItem));
        when(listingRepository.findByUserCardIdAndListingStatus(offeredCard.getId(), ListingStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(listingRepository.findByUserCardIdAndListingStatus(requestedCard.getId(), ListingStatus.ACTIVE))
                .thenReturn(Optional.of(listing));

        var response = tradeOfferService.accept(tradeOffer.getId());

        assertThat(response.status()).isEqualTo(TradeStatus.ACCEPTED.name());
        assertThat(offeredCard.getOwner()).isSameAs(receiver);
        assertThat(requestedCard.getOwner()).isSameAs(sender);
        assertThat(offeredCard.getStatus()).isEqualTo(UserCardStatus.AVAILABLE);
        assertThat(requestedCard.getStatus()).isEqualTo(UserCardStatus.AVAILABLE);
        assertThat(listing.getListingStatus()).isEqualTo(ListingStatus.CANCELED);

        verify(userCardRepository).saveAndFlush(offeredCard);
        verify(userCardRepository).saveAndFlush(requestedCard);
        verify(listingRepository).saveAndFlush(listing);
        verify(tradeOfferRepository).saveAndFlush(tradeOffer);
    }

    @Test
    void acceptRejectsTradeWithoutItems() {
        User sender = user("Ash");
        User receiver = user("Misty");
        TradeOffer tradeOffer = tradeOffer(sender, receiver);

        when(tradeOfferRepository.findById(tradeOffer.getId())).thenReturn(Optional.of(tradeOffer));
        when(authService.getAuthenticatedUser()).thenReturn(receiver);
        when(tradeOfferItemRepository.findByTradeOfferIdWithCards(tradeOffer.getId())).thenReturn(List.of());

        assertThatThrownBy(() -> tradeOfferService.accept(tradeOffer.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Proposta de troca sem cartas vinculadas");
    }

    private User user(String name) {
        return User.builder().id(UUID.randomUUID()).name(name).credits(1000L).build();
    }

    private UserCard userCard(User owner, UserCardStatus status) {
        return UserCard.builder()
                .id(UUID.randomUUID())
                .owner(owner)
                .status(status)
                .build();
    }

    private TradeOffer tradeOffer(User sender, User receiver) {
        return TradeOffer.builder()
                .id(UUID.randomUUID())
                .sender(sender)
                .receiver(receiver)
                .status(TradeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private TradeOfferItem item(TradeOffer tradeOffer, UserCard userCard, TradeItemType type) {
        return TradeOfferItem.builder()
                .id(UUID.randomUUID())
                .tradeOffer(tradeOffer)
                .userCard(userCard)
                .type(type)
                .build();
    }

    private Listing listing(User seller, UserCard userCard) {
        return Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard)
                .listingType(ListingType.SALE)
                .listingStatus(ListingStatus.ACTIVE)
                .price(250L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
