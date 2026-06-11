package com.pocketmarket.purchase;

import com.pocketmarket.enums.PurchaseStatus;
import com.pocketmarket.enums.PurchaseType;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCard;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_card_id", nullable = false)
    private UserCard userCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus status = PurchaseStatus.COMPLETED;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long sellerAmount;

    @Column(nullable = false)
    private Long platformFee;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = PurchaseStatus.COMPLETED;
        }
    }
}
