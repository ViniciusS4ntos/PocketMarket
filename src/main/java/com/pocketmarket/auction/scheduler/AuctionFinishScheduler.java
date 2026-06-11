package com.pocketmarket.auction.scheduler;

import com.pocketmarket.auction.service.AuctionBidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionFinishScheduler {

    private final AuctionBidService auctionBidService;

    @Scheduled(fixedDelay = 60000)
    public void finishExpiredAuctions() {
        var expiredAuctionIds = auctionBidService.findExpiredActiveAuctionIds();

        if (expiredAuctionIds.isEmpty()) {
            return;
        }

        for (UUID listingId : expiredAuctionIds) {
            try {
                auctionBidService.finishAuction(listingId);
                log.info("Leilão finalizado automaticamente. listingId={}",  listingId);
            } catch (Exception exception) {
                log.warn(
                        "Erro ao finalizar leilão automaticamente. listingId={}",
                        listingId,
                        exception
                );
            }
        }
    }
}
