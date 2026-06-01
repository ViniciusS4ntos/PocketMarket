package com.pocketmarket.trade;

import com.pocketmarket.trade.dto.CreateTradeOfferRequest;
import com.pocketmarket.trade.dto.TradeOfferResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trade-offers")
public class TradeOfferController {

    private final TradeOfferService tradeOfferService;

    public TradeOfferController(
            TradeOfferService tradeOfferService
    ) {
        this.tradeOfferService = tradeOfferService;
    }

    @PostMapping
    public ResponseEntity<TradeOfferResponse> create(
            @RequestBody CreateTradeOfferRequest request
    ) {

        return ResponseEntity.ok(
                tradeOfferService.create(request)
        );
    }

    @GetMapping("/sent")
    public ResponseEntity<List<TradeOfferResponse>> sent() {

        return ResponseEntity.ok(
                tradeOfferService.getSent()
        );
    }

    @GetMapping("/received")
    public ResponseEntity<List<TradeOfferResponse>> received() {

        return ResponseEntity.ok(
                tradeOfferService.getReceived()
        );
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<TradeOfferResponse> accept(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                tradeOfferService.accept(id)
        );
    }
}