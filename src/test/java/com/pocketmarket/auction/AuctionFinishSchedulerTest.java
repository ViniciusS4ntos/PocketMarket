package com.pocketmarket.auction;

import com.pocketmarket.auction.scheduler.AuctionFinishScheduler;
import com.pocketmarket.auction.service.AuctionBidService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuctionFinishSchedulerTest {

    @Test
    void finishExpiredAuctionsDoesNothingWhenThereAreNoExpiredAuctions() {
        AuctionBidService service = mock(AuctionBidService.class);
        AuctionFinishScheduler scheduler = new AuctionFinishScheduler(service);
        when(service.findExpiredActiveAuctionIds()).thenReturn(List.of());

        scheduler.finishExpiredAuctions();

        verify(service, never()).finishAuction(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void finishExpiredAuctionsFinishesEachExpiredAuctionAndContinuesAfterFailure() {
        AuctionBidService service = mock(AuctionBidService.class);
        AuctionFinishScheduler scheduler = new AuctionFinishScheduler(service);
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        when(service.findExpiredActiveAuctionIds()).thenReturn(List.of(first, second));
        doThrow(new RuntimeException("boom")).when(service).finishAuction(first);

        scheduler.finishExpiredAuctions();

        verify(service).finishAuction(first);
        verify(service).finishAuction(second);
    }
}
