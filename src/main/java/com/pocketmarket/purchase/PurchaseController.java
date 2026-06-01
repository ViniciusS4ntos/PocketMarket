package com.pocketmarket.purchase;

import com.pocketmarket.purchase.dto.response.PurchaseResponse;
import com.pocketmarket.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    
    private final PurchaseService purchaseService;

    @GetMapping("/my-purchases")
    public Page<PurchaseResponse> listMyPurchases(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pageable) {
        return purchaseService.listMyPurchases(user, pageable);
    }

    @GetMapping("/my-sales")
    public Page<PurchaseResponse> listMySales(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pageable) {
        return purchaseService.listMySales(user, pageable);
    }

    @PostMapping("/{listingId}/buy")
    public PurchaseResponse buyListing(@AuthenticationPrincipal User user, @PathVariable UUID listingId) {
        return purchaseService.buyListing(user, listingId);
    }
}
