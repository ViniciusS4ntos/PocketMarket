package com.pocketmarket.auction.service;

import com.pocketmarket.auction.AuctionBid;
import com.pocketmarket.auction.dto.response.AuctionFinishResponse;
import com.pocketmarket.auction.mapper.AuctionFinishMapper;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.listing.ListingRepository;
import com.pocketmarket.purchase.Purchase;
import com.pocketmarket.purchase.PurchaseMapper;
import com.pocketmarket.purchase.PurchaseRepository;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionFinishService {

    private static final long PLATFORM_FEE_PERCENTAGE = 10L;

    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;
    private final ListingRepository listingRepository;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public AuctionFinishResponse withWinner(Listing listing, AuctionBid winningBid) {

        Long platformFee = calculatePlatformFee(winningBid.getAmount());
        Long sellerAmount = winningBid.getAmount() - platformFee;

        listing.getSeller().setCredits(listing.getSeller().getCredits() + sellerAmount);

        listing.getUserCard().setOwner(winningBid.getBidder());
        listing.getUserCard().setStatus(UserCardStatus.AVAILABLE);

        listing.setListingStatus(ListingStatus.SOLD);
        listing.setCurrentBid(winningBid.getAmount());
        listing.setCurrentBidder(winningBid.getBidder());

        Purchase purchase = PurchaseMapper.toAuctionPurchase(listing, winningBid, platformFee, sellerAmount);

        userRepository.save(purchase.getSeller());
        userCardRepository.save(purchase.getListing().getUserCard());
        listingRepository.save(purchase.getListing());

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return AuctionFinishMapper.toResponse(savedPurchase);
    }

    @Transactional
    public AuctionFinishResponse withoutWinner(Listing listing) {

        UserCard userCard = listing.getUserCard();

        listing.setListingStatus(ListingStatus.EXPIRED);
        listing.setCurrentBidder(null);

        userCard.setStatus(UserCardStatus.AVAILABLE);

        userCardRepository.save(userCard);
        listingRepository.save(listing);

        return AuctionFinishMapper.toResponseWithoutWinner(listing);
    }

    public static Long calculatePlatformFee(Long amount) {
        return amount * PLATFORM_FEE_PERCENTAGE / 100;
    }
}
