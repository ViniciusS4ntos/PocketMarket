package com.pocketmarket.auction;

import com.pocketmarket.auction.dto.request.AuctionBidRequest;
import com.pocketmarket.auction.dto.response.AuctionBidResponse;
import com.pocketmarket.auction.dto.response.AuctionFinishResponse;
import com.pocketmarket.auction.service.AuctionBidService;
import com.pocketmarket.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionBidController {

    private final AuctionBidService auctionBidService;

    @PostMapping("/{listingId}/bids")
    @ResponseStatus(HttpStatus.CREATED)
    public AuctionBidResponse createBid(@AuthenticationPrincipal User currentUser, @PathVariable UUID listingId, @RequestBody AuctionBidRequest request) {
        return auctionBidService.createBid(currentUser, listingId, request);
    }

    @GetMapping("/{listingId}/bids")
    public Page<AuctionBidResponse> getBids(@PathVariable UUID listingId, @PageableDefault Pageable pageable) {
        return auctionBidService.getBids(listingId, pageable);
    }

    @PostMapping("/{listingId}/finish")
    public AuctionFinishResponse finishAuction(@PathVariable UUID listingId) {
        return auctionBidService.finishAuction(listingId);
    }
}
