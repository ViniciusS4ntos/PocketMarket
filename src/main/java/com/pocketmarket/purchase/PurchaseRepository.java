package com.pocketmarket.purchase;

import com.pocketmarket.enums.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    Page<Purchase> findByBuyerIdAndStatus(UUID id, PurchaseStatus purchaseStatus, Pageable pageable);

    Page<Purchase> findBySellerId(UUID id, Pageable pageable);
}
