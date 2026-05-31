package com.pocketmarket.auction;

import com.pocketmarket.enums.AuctionBidStatus;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "auction_bid",
        indexes = {
                @Index(name = "idx_auction_bids_listing_id", columnList = "listing_id"),
                @Index(name = "idx_auction_bids_bidder_id", columnList = "bidder_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionBid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AuctionBidStatus status = AuctionBidStatus.WINNING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = AuctionBidStatus.WINNING;
        }
    }
}
