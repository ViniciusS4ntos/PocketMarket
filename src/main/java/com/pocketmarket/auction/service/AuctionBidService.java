package com.pocketmarket.auction.service;

import com.pocketmarket.auction.AuctionBid;
import com.pocketmarket.auction.mapper.AuctionBidMapper;
import com.pocketmarket.auction.AuctionBidRepository;
import com.pocketmarket.auction.validator.AuctionBidValidator;
import com.pocketmarket.auction.dto.request.AuctionBidRequest;
import com.pocketmarket.auction.dto.response.AuctionBidResponse;
import com.pocketmarket.auction.dto.response.AuctionFinishResponse;
import com.pocketmarket.auction.validator.AuctionCanBeFinishedValidator;
import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.listing.ListingRepository;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionBidService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final AuctionBidRepository auctionBidRepository;

    private final AuctionBidValidator auctionBidValidator;
    private final AuctionFinishService auctionFinishService;
    private final AuctionCanBeFinishedValidator auctionCanBeFinishedValidator;

    @Transactional
    public AuctionBidResponse createBid(User currentUser, UUID id, AuctionBidRequest request) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leilao nao encontrado!"));

        User bidder = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado!"));

        auctionBidValidator.validate(listing, bidder, request.amount());

        auctionBidRepository.findByListingIdAndStatus(
                id,
                AuctionBidStatus.WINNING
        ).ifPresent(previousWinningBid -> {
            User previousBidder = previousWinningBid.getBidder();

            previousBidder.setCredits(previousBidder.getCredits() + previousWinningBid.getAmount());
            previousWinningBid.setStatus(AuctionBidStatus.OUTBID);

            userRepository.save(previousBidder);
            auctionBidRepository.save(previousWinningBid);
        });

        bidder.setCredits(bidder.getCredits() - request.amount());

        AuctionBid newBid = AuctionBidMapper.toEntity(bidder, listing, request);

        listing.setCurrentBid(request.amount());
        listing.setCurrentBidder(bidder);

        userRepository.save(bidder);
        listingRepository.save(listing);

        AuctionBid savedBid = auctionBidRepository.save(newBid);

        return AuctionBidMapper.toResponse(savedBid);
    }

    public Page<AuctionBidResponse> getBids(UUID listingId, Pageable pageable) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Leilao nao encontrado!"));

        if (listing.getListingType() != ListingType.AUCTION) {
            throw new RuntimeException("Este anúncio não é um leilão.");
        }

        return auctionBidRepository.findByListing(listing, pageable)
                .map(AuctionBidMapper::toResponse);
    }

    @Transactional
    public AuctionFinishResponse finishAuction(UUID listingId) {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new RuntimeException("Leilao nao encontrado!"));

            auctionCanBeFinishedValidator.validate(listing);

            return auctionBidRepository
                    .findByListingIdAndStatus(listing.getId(), AuctionBidStatus.WINNING)
                    .map(winningBid -> auctionFinishService.withWinner(listing, winningBid))
                    .orElseGet(() -> auctionFinishService.withoutWinner(listing));
    }

    @Transactional(readOnly = true)
    public List<UUID> findExpiredActiveAuctionIds() {
        return listingRepository.findExpiredActiveAuctionIds(
                ListingType.AUCTION,
                ListingStatus.ACTIVE,
                LocalDateTime.now()
        );
    }
}
