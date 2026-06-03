package com.pocketmarket.listing;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID> {
    Page<Listing> findByListingStatus(ListingStatus listingStatus, Pageable pageable);

    @Query("""
            SELECT l.id
            FROM Listing l
            WHERE l.listingType = :listingType
            AND l.listingStatus = :listingStatus
            AND l.auctionEndsAt <= :now
            """)
    List<UUID> findExpiredActiveAuctionIds(
            ListingType listingType,
            ListingStatus listingStatus,
            LocalDateTime now
    );
}
