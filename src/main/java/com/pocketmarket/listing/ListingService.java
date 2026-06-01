package com.pocketmarket.listing;

import com.pocketmarket.auction.AuctionBidRepository;
import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.listing.dto.request.ListingAuctionRequest;
import com.pocketmarket.listing.dto.request.ListingSaleRequest;
import com.pocketmarket.listing.dto.response.ListingAuctionResponse;
import com.pocketmarket.listing.dto.response.ListingResponse;
import com.pocketmarket.listing.dto.response.ListingSaleResponse;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final UserCardRepository  userCardRepository;
    private final AuctionBidRepository auctionBidRepository;

    @Transactional
    public ListingSaleResponse postListingSale(User currentUser, UUID userCardId, ListingSaleRequest request) {

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado!"));

        UserCard userCard = userCardRepository.findById(userCardId)
                .orElseThrow(() -> new RuntimeException("UserCard nao encontrado!"));

        if (!userCard.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Voce nao tem permissao para listar este card!");
        }

        if (userCard.getStatus() != UserCardStatus.AVAILABLE) {
            throw new RuntimeException("Este card nao esta disponivel para listagem!");
        }

        userCard.setStatus(UserCardStatus.LISTED);
        userCardRepository.save(userCard);

        Listing listing = ListingMapper.toSaleEntity(user, userCard, request);

        listingRepository.save(listing);

        return ListingMapper.toSaleResponse(listing);
    }

    @Transactional
    public ListingAuctionResponse postListingAuction(User currentUser, UUID userCardId, @Valid ListingAuctionRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado!"));

        UserCard userCard = userCardRepository.findById(userCardId)
                .orElseThrow(() -> new RuntimeException("UserCard nao encontrado!"));

        if (!userCard.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Voce nao tem permissao para listar este card!");
        }

        if (userCard.getStatus() != UserCardStatus.AVAILABLE) {
            throw new RuntimeException("Este card nao esta disponivel para listagem!");
        }

        if (request.startingBid() == null || request.startingBid() <= 0) {
            throw new RuntimeException("O lance inicial deve ser maior que zero.");
        }

        if (request.minBidIncrement() == null || request.minBidIncrement() <= 0) {
            throw new RuntimeException("O incremento mínimo deve ser maior que zero.");
        }

        if (request.auctionEndsAt() == null || !request.auctionEndsAt().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("A data de fim do leilão deve ser futura.");
        }

        userCard.setStatus(UserCardStatus.LISTED);
        userCardRepository.save(userCard);

        Listing auction = ListingMapper.toAuctionEntity(user, userCard, request);

        listingRepository.save(auction);

        return ListingMapper.toAuctionResponse(auction);
    }

    public Page<ListingResponse> showListings(Pageable pageable) {

            return listingRepository.findByListingStatus(ListingStatus.ACTIVE, pageable)
                    .map(ListingMapper::toResponse);
    }

    public ListingResponse showListing(UUID id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing nao encontrado!"));

        return ListingMapper.toResponse(listing);
    }

    @Transactional
    public ListingResponse cancelListing(User currentUser, UUID id) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado!"));

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing nao encontrado!"));

        if (!listing.getSeller().getId().equals(user.getId())) {
            throw new RuntimeException("Voce nao tem permissao para cancelar este listing!");
        }

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new RuntimeException("Apenas anúncios ativos podem ser cancelados.");
        }

        if (listing.getListingType() == ListingType.AUCTION) {
            cancelAuctionBidIfExists(listing);
        }

        listing.setListingStatus(ListingStatus.CANCELED);
        listing.getUserCard().setStatus(UserCardStatus.AVAILABLE);

        userCardRepository.save(listing.getUserCard());
        listingRepository.save(listing);

        return ListingMapper.toResponse(listing);
    }

    private void cancelAuctionBidIfExists(Listing listing) {
        auctionBidRepository
                .findByListingIdAndStatus(listing.getId(), AuctionBidStatus.WINNING)
                .ifPresent(winningBid -> {
                    User bidder = winningBid.getBidder();

                    bidder.setCredits(bidder.getCredits() + winningBid.getAmount());

                    winningBid.setStatus(AuctionBidStatus.CANCELLED);

                    listing.setCurrentBidder(null);

                    userRepository.save(bidder);
                    auctionBidRepository.save(winningBid);
                });
    }
}
