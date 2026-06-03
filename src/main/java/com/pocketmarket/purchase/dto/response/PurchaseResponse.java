package com.pocketmarket.purchase.dto.response;

import com.pocketmarket.enums.PurchaseStatus;
import com.pocketmarket.enums.PurchaseType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PurchaseResponse(
        UUID id,
        UUID listingId,
        UUID userCardId,
        UUID buyerId,
        String buyerName,
        UUID sellerId,
        String sellerName,
        PurchaseType type,
        PurchaseStatus status,
        Long amount,
        Long sellerAmount,
        Long platformFee,
        LocalDateTime createdAt
) {
}
