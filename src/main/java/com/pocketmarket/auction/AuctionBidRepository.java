package com.pocketmarket.auction;

import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.listing.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, UUID> {
    Page<AuctionBid> findByListing(Listing listing, Pageable pageable);

    List<AuctionBid> findByListingIdOrderByCreatedAtDesc(UUID listingId);

    Optional<AuctionBid> findByListingIdAndStatus(UUID id, AuctionBidStatus auctionBidStatus);
}
