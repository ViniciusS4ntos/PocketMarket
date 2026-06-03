package com.pocketmarket.auction.validator;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.listing.Listing;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuctionCanBeFinishedValidator {

    public void validate(Listing listing) {

        if (listing.getListingType() != ListingType.AUCTION) {
            throw new RuntimeException("Este anúncio não é um leilão.");
        }

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new RuntimeException("Este leilão não está ativo.");
        }

        if (listing.getAuctionEndsAt() == null) {
            throw new RuntimeException("Data de encerramento do leilão não definida.");
        }

        if (listing.getAuctionEndsAt().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Este leilão ainda não terminou.");
        }
    }
}
