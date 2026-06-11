package com.pocketmarket.purchase;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.PurchaseStatus;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.listing.ListingRepository;
import com.pocketmarket.purchase.dto.response.PurchaseResponse;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.pocketmarket.auction.service.AuctionFinishService.calculatePlatformFee;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final UserRepository  userRepository;
    private final PurchaseRepository purchaseRepository;
    private final ListingRepository listingRepository;
    private final UserCardRepository userCardRepository;

    private final BuyListingValidator buyListingValidator;

    public Page<PurchaseResponse> listMyPurchases(User user, Pageable pageable) {
        User currentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return purchaseRepository.findByBuyerIdAndStatus(currentUser.getId(), PurchaseStatus.COMPLETED, pageable)
                .map(PurchaseMapper::toResponse);
    }

    public Page<PurchaseResponse> listMySales(User user, Pageable pageable) {
        User currentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return purchaseRepository.findBySellerId(currentUser.getId(), pageable)
                .map(PurchaseMapper::toResponse);
    }

    @Transactional
    public PurchaseResponse buyListing(User user, UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        buyListingValidator.validate(user, listing);

        User buyer = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long platformFee = calculatePlatformFee(listing.getPrice());
        Long sellerAmount = listing.getPrice() - platformFee;

        buyer.setCredits(buyer.getCredits() - listing.getPrice());
        listing.getSeller().setCredits(listing.getSeller().getCredits() + sellerAmount);

        listing.getUserCard().setOwner(buyer);
        listing.getUserCard().setStatus(UserCardStatus.AVAILABLE);

        listing.setListingStatus(ListingStatus.SOLD);

        Purchase purchase = PurchaseMapper.toSalePurchase(
                listing,
                buyer,
                sellerAmount,
                platformFee
        );

        userRepository.save(buyer);
        userRepository.save(listing.getSeller());
        userCardRepository.save(listing.getUserCard());
        listingRepository.save(listing);

        Purchase savedPurchase = purchaseRepository.save(purchase);
        return PurchaseMapper.toResponse(savedPurchase);
    }
}
