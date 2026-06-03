package com.pocketmarket.purchase;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.enums.PurchaseStatus;
import com.pocketmarket.enums.UserCardStatus;
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
class PurchaseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private UserCardRepository userCardRepository;

    @Mock
    private BuyListingValidator buyListingValidator;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void listMyPurchasesReturnsCompletedPurchases() {
        User buyer = user(1000L);
        Purchase purchase = purchase(buyer, user(0L));
        PageRequest pageable = PageRequest.of(0, 10);

        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(purchaseRepository.findByBuyerIdAndStatus(buyer.getId(), PurchaseStatus.COMPLETED, pageable))
                .thenReturn(new PageImpl<>(List.of(purchase), pageable, 1));

        var response = purchaseService.listMyPurchases(buyer, pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().buyerId()).isEqualTo(buyer.getId());
    }

    @Test
    void listMySalesReturnsSellerPurchases() {
        User seller = user(0L);
        Purchase purchase = purchase(user(1000L), seller);
        PageRequest pageable = PageRequest.of(0, 10);

        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(purchaseRepository.findBySellerId(seller.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(purchase), pageable, 1));

        var response = purchaseService.listMySales(seller, pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().sellerId()).isEqualTo(seller.getId());
    }

    @Test
    void listMyPurchasesAndSalesThrowWhenUserDoesNotExist() {
        User user = user(0L);
        PageRequest pageable = PageRequest.of(0, 10);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.listMyPurchases(user, pageable))
                .hasMessage("User not found");

        assertThatThrownBy(() -> purchaseService.listMySales(user, pageable))
                .hasMessage("User not found");
    }

    @Test
    void buyListingTransfersCreditsCardAndCreatesPurchase() {
        User buyer = user(1000L);
        User seller = user(100L);
        UserCard userCard = UserCard.builder().id(UUID.randomUUID()).owner(seller).status(UserCardStatus.LISTED).build();
        Listing listing = saleListing(seller, userCard, 500L);

        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(purchaseRepository.save(org.mockito.ArgumentMatchers.any(Purchase.class)))
                .thenAnswer(invocation -> {
                    Purchase purchase = invocation.getArgument(0);
                    purchase.setId(UUID.randomUUID());
                    purchase.setCreatedAt(LocalDateTime.now());
                    return purchase;
                });

        var response = purchaseService.buyListing(buyer, listing.getId());

        assertThat(response.amount()).isEqualTo(500L);
        assertThat(response.platformFee()).isEqualTo(50L);
        assertThat(response.sellerAmount()).isEqualTo(450L);
        assertThat(buyer.getCredits()).isEqualTo(500L);
        assertThat(seller.getCredits()).isEqualTo(550L);
        assertThat(userCard.getOwner()).isSameAs(buyer);
        assertThat(userCard.getStatus()).isEqualTo(UserCardStatus.AVAILABLE);
        assertThat(listing.getListingStatus()).isEqualTo(ListingStatus.SOLD);
        verify(buyListingValidator).validate(buyer, listing);
        verify(userRepository).save(buyer);
        verify(userRepository).save(seller);
        verify(userCardRepository).save(userCard);
        verify(listingRepository).save(listing);
    }

    @Test
    void buyListingThrowsWhenListingOrBuyerDoesNotExist() {
        User buyer = user(1000L);
        UUID listingId = UUID.randomUUID();
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.buyListing(buyer, listingId))
                .hasMessage("Listing not found");

        User seller = user(100L);
        Listing listing = saleListing(seller, UserCard.builder().id(UUID.randomUUID()).owner(seller).status(UserCardStatus.LISTED).build(), 500L);
        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.buyListing(buyer, listing.getId()))
                .hasMessage("User not found");
    }

    private Purchase purchase(User buyer, User seller) {
        UserCard userCard = UserCard.builder().id(UUID.randomUUID()).owner(buyer).build();
        Listing listing = Listing.builder().id(UUID.randomUUID()).seller(seller).userCard(userCard).build();

        return Purchase.builder()
                .id(UUID.randomUUID())
                .listing(listing)
                .userCard(userCard)
                .buyer(buyer)
                .seller(seller)
                .type(com.pocketmarket.enums.PurchaseType.SALE)
                .status(PurchaseStatus.COMPLETED)
                .amount(500L)
                .sellerAmount(450L)
                .platformFee(50L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Listing saleListing(User seller, UserCard userCard, Long price) {
        return Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(userCard)
                .listingType(ListingType.SALE)
                .listingStatus(ListingStatus.ACTIVE)
                .price(price)
                .build();
    }

    private User user(Long credits) {
        return User.builder().id(UUID.randomUUID()).name("Ash").credits(credits).build();
    }
}
