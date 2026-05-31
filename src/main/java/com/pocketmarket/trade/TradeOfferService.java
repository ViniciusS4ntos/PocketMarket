package com.pocketmarket.trade;

import com.pocketmarket.auth.AuthService;
import com.pocketmarket.enums.TradeItemType;
import com.pocketmarket.enums.TradeStatus;
import com.pocketmarket.trade.dto.CreateTradeOfferRequest;
import com.pocketmarket.trade.dto.TradeOfferResponse;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.UserCard;
import com.pocketmarket.usercards.UserCardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TradeOfferService {

    private final TradeOfferRepository tradeOfferRepository;
    private final TradeOfferItemRepository tradeOfferItemRepository;
    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;
    private final AuthService authService;

    public TradeOfferService(
            TradeOfferRepository tradeOfferRepository,
            TradeOfferItemRepository tradeOfferItemRepository,
            UserRepository userRepository,
            UserCardRepository userCardRepository,
            AuthService authService
    ) {
        this.tradeOfferRepository = tradeOfferRepository;
        this.tradeOfferItemRepository = tradeOfferItemRepository;
        this.userRepository = userRepository;
        this.userCardRepository = userCardRepository;
        this.authService = authService;
    }

    public TradeOfferResponse create(
            CreateTradeOfferRequest request
    ) {

        User sender = authService.getAuthenticatedUser();

        User receiver = userRepository.findById(
                request.receiverId()
        ).orElseThrow(() ->
                new RuntimeException("Usuário destinatário não encontrado")
        );

        TradeOffer tradeOffer = TradeOffer.builder()
                .sender(sender)
                .receiver(receiver)
                .status(TradeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        tradeOfferRepository.save(tradeOffer);

        // Cartas ofertadas
        for (UUID cardId : request.offeredCardIds()) {

            UserCard userCard = userCardRepository
                    .findById(cardId)
                    .orElseThrow(() ->
                            new RuntimeException("Carta não encontrada")
                    );

            if (!userCard.getOwner().getId().equals(sender.getId())) {
                throw new RuntimeException(
                        "Você não é proprietário da carta ofertada"
                );
            }

            TradeOfferItem item = TradeOfferItem.builder()
                    .tradeOffer(tradeOffer)
                    .userCard(userCard)
                    .type(TradeItemType.OFFERED)
                    .build();

            tradeOfferItemRepository.save(item);
        }

        // Cartas solicitadas
        for (UUID cardId : request.requestedCardIds()) {

            UserCard userCard = userCardRepository
                    .findById(cardId)
                    .orElseThrow(() ->
                            new RuntimeException("Carta não encontrada")
                    );

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

    public TradeOfferResponse accept(UUID id) {

        TradeOffer tradeOffer = tradeOfferRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Oferta não encontrada")
                );

        tradeOffer.setStatus(TradeStatus.ACCEPTED);

        tradeOfferRepository.save(tradeOffer);

        return mapToResponse(tradeOffer);
    }

    private TradeOfferResponse mapToResponse(
            TradeOffer tradeOffer
    ) {
        return new TradeOfferResponse(
                tradeOffer.getId(),
                tradeOffer.getSender().getName(),
                tradeOffer.getReceiver().getName(),
                tradeOffer.getStatus().name()
        );
    }
}