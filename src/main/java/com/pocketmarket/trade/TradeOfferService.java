package com.pocketmarket.trade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pocketmarket.auction.AuctionBidRepository;
import com.pocketmarket.auth.AuthService;
import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.enums.TradeItemType;
import com.pocketmarket.enums.TradeStatus;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.exceptions.ForbiddenException;
import com.pocketmarket.exceptions.NotFoundException;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.listing.ListingRepository;
import com.pocketmarket.trade.dto.CreateTradeOfferRequest;
import com.pocketmarket.trade.dto.TradeOfferResponse;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;

@Service
public class TradeOfferService {

    private final TradeOfferRepository tradeOfferRepository;
    private final TradeOfferItemRepository tradeOfferItemRepository;
    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;
    private final AuthService authService;
    private final ListingRepository listingRepository;
    private final AuctionBidRepository auctionBidRepository;

    public TradeOfferService(
            TradeOfferRepository tradeOfferRepository,
            TradeOfferItemRepository tradeOfferItemRepository,
            UserRepository userRepository,
            UserCardRepository userCardRepository,
            AuthService authService,
            ListingRepository listingRepository,
            AuctionBidRepository auctionBidRepository
    ) {
        this.tradeOfferRepository = tradeOfferRepository;
        this.tradeOfferItemRepository = tradeOfferItemRepository;
        this.userRepository = userRepository;
        this.userCardRepository = userCardRepository;
        this.authService = authService;
        this.listingRepository = listingRepository;
        this.auctionBidRepository = auctionBidRepository;
    }

    @Transactional
    public TradeOfferResponse create(CreateTradeOfferRequest request) {

        User sender = authService.getAuthenticatedUser();

        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new NotFoundException("Usuário destinatário não encontrado"));

        TradeOffer tradeOffer = TradeOffer.builder()
                .sender(sender)
                .receiver(receiver)
                .status(TradeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        tradeOfferRepository.save(tradeOffer);

        for (UUID cardId : request.offeredCardIds()) {
            UserCard userCard = userCardRepository.findById(cardId)
                    .orElseThrow(() -> new NotFoundException("Carta não encontrada"));

            if (!userCard.getOwner().getId().equals(sender.getId())) {
                throw new ForbiddenException("Você não é proprietário da carta ofertada");
            }

            TradeOfferItem item = TradeOfferItem.builder()
                    .tradeOffer(tradeOffer)
                    .userCard(userCard)
                    .type(TradeItemType.OFFERED)
                    .build();

            tradeOfferItemRepository.save(item);
        }

        for (UUID cardId : request.requestedCardIds()) {
            UserCard userCard = userCardRepository.findById(cardId)
                    .orElseThrow(() -> new NotFoundException("Carta não encontrada"));

            if (!userCard.getOwner().getId().equals(receiver.getId())) {
                throw new ForbiddenException("Carta solicitada não pertence ao destinatário");
            }

            TradeOfferItem item = TradeOfferItem.builder()
                    .tradeOffer(tradeOffer)
                    .userCard(userCard)
                    .type(TradeItemType.REQUESTED)
                    .build();

            tradeOfferItemRepository.save(item);
        }

        return mapToResponse(tradeOffer);
    }

    public List<TradeOfferResponse> getSent() {
        User sender = authService.getAuthenticatedUser();
        return tradeOfferRepository.findBySender(sender)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TradeOfferResponse> getReceived() {
        User receiver = authService.getAuthenticatedUser();
        return tradeOfferRepository.findByReceiver(receiver)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public TradeOfferResponse accept(UUID id) {
        TradeOffer tradeOffer = tradeOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Oferta não encontrada"));

        User currentUser = authService.getAuthenticatedUser();

        if (!tradeOffer.getReceiver().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Você não tem permissão para aceitar esta proposta");
        }

        if (tradeOffer.getStatus() != TradeStatus.PENDING) {
            throw new ForbiddenException("Apenas propostas pendentes pogidem ser aceitas");
        }

        List<TradeOfferItem> items = tradeOfferItemRepository.findByTradeOfferIdWithCards(tradeOffer.getId());

        if (items.isEmpty()) {
            throw new ForbiddenException("Proposta de troca sem cartas vinculadas");
        }

        for (TradeOfferItem item : items) {
            UserCard userCard = item.getUserCard();
            validateTradeOwnership(tradeOffer, item);
            cancelActiveListingIfExists(userCard);

            if (item.getType() == TradeItemType.OFFERED) {
                userCard.setOwner(tradeOffer.getReceiver());
            } else if (item.getType() == TradeItemType.REQUESTED) {
                userCard.setOwner(tradeOffer.getSender());
            }

            userCard.setStatus(UserCardStatus.AVAILABLE);
            userCardRepository.saveAndFlush(userCard);
        }

        tradeOffer.setStatus(TradeStatus.ACCEPTED);
        tradeOfferRepository.saveAndFlush(tradeOffer);

        return mapToResponse(tradeOffer);
    }

    private void validateTradeOwnership(TradeOffer tradeOffer, TradeOfferItem item) {
        UserCard userCard = item.getUserCard();

        if (item.getType() == TradeItemType.OFFERED
                && !userCard.getOwner().getId().equals(tradeOffer.getSender().getId())) {
            throw new ForbiddenException("Carta ofertada não pertence mais ao remetente");
        }

        if (item.getType() == TradeItemType.REQUESTED
                && !userCard.getOwner().getId().equals(tradeOffer.getReceiver().getId())) {
            throw new ForbiddenException("Carta solicitada não pertence mais ao destinatário");
        }
    }

    private void cancelActiveListingIfExists(UserCard userCard) {
        listingRepository
                .findByUserCardIdAndListingStatus(userCard.getId(), ListingStatus.ACTIVE)
                .ifPresent(listing -> {
                    if (listing.getListingType() == ListingType.AUCTION) {
                        cancelAuctionBidIfExists(listing);
                    }

                    listing.setListingStatus(ListingStatus.CANCELED);
                    listingRepository.saveAndFlush(listing);
                });
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

    private TradeOfferResponse mapToResponse(TradeOffer tradeOffer) {
        return new TradeOfferResponse(
                tradeOffer.getId(),
                tradeOffer.getSender().getName(),
                tradeOffer.getReceiver().getName(),
                tradeOffer.getStatus().name()
        );
    }
}
