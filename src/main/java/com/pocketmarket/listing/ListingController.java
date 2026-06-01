package com.pocketmarket.listing;

import com.pocketmarket.listing.dto.request.ListingAuctionRequest;
import com.pocketmarket.listing.dto.request.ListingSaleRequest;
import com.pocketmarket.listing.dto.response.ListingAuctionResponse;
import com.pocketmarket.listing.dto.response.ListingResponse;
import com.pocketmarket.listing.dto.response.ListingSaleResponse;
import com.pocketmarket.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @PostMapping("/sale/{userCardId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ListingSaleResponse postListingSale(@AuthenticationPrincipal User currentUser, @PathVariable UUID userCardId, @RequestBody @Valid ListingSaleRequest request) {
        return listingService.postListingSale(currentUser, userCardId, request);
    }

    @PostMapping("/auction/{userCardId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ListingAuctionResponse postListingAuction(@AuthenticationPrincipal User currentUser, @PathVariable UUID userCardId, @RequestBody @Valid ListingAuctionRequest request) {
        return listingService.postListingAuction(currentUser, userCardId, request);
    }

    @GetMapping
    public Page<ListingResponse> showListings(@PageableDefault(size = 20) Pageable pageable) {
        return listingService.showListings(pageable);
    }

    @GetMapping("/{id}")
    public ListingResponse showListing(@PathVariable UUID id) {
        return listingService.showListing(id);
    }

    @PatchMapping("/{id}/cancel")
    public ListingResponse cancelListing(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
        return listingService.cancelListing(currentUser, id);
    }
}
