package com.pocketmarket.auction.validator;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.exceptions.BusinessException;
import com.pocketmarket.listing.Listing;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuctionCanBeFinishedValidator {

    public void validate(Listing listing) {

        if (listing.getListingType() != ListingType.AUCTION) {
            throw new BusinessException("Este anúncio não é um leilão.");
        }

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("Este leilão não está ativo.");
        }

        if (listing.getAuctionEndsAt() == null) {
            throw new BusinessException("Data de encerramento do leilão não definida.");
        }

        if (listing.getAuctionEndsAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Este leilão ainda não terminou.");
        }
    }
}