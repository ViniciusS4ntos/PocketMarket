package com.pocketmarket.auction;

import com.pocketmarket.auction.validator.AuctionCanBeFinishedValidator;
import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.listing.Listing;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuctionCanBeFinishedValidatorTest {

    private final AuctionCanBeFinishedValidator validator = new AuctionCanBeFinishedValidator();

    @Test
    void validateAcceptsFinishedActiveAuction() {
        assertThatCode(() -> validator.validate(auction(LocalDateTime.now().minusMinutes(1))))
                .doesNotThrowAnyException();
    }

    @Test
    void validateRejectsInvalidFinishState() {
        Listing sale = auction(LocalDateTime.now().minusMinutes(1));
        sale.setListingType(ListingType.SALE);
        assertThatThrownBy(() -> validator.validate(sale)).hasMessage("Este anúncio não é um leilão.");

        Listing canceled = auction(LocalDateTime.now().minusMinutes(1));
        canceled.setListingStatus(ListingStatus.CANCELED);
        assertThatThrownBy(() -> validator.validate(canceled)).hasMessage("Este leilão não está ativo.");

        Listing withoutEndDate = auction(null);
        assertThatThrownBy(() -> validator.validate(withoutEndDate)).hasMessage("Data de encerramento do leilão não definida.");

        Listing notFinished = auction(LocalDateTime.now().plusMinutes(1));
        assertThatThrownBy(() -> validator.validate(notFinished)).hasMessage("Este leilão ainda não terminou.");
    }

    private Listing auction(LocalDateTime endsAt) {
        return Listing.builder()
                .listingType(ListingType.AUCTION)
                .listingStatus(ListingStatus.ACTIVE)
                .auctionEndsAt(endsAt)
                .build();
    }
}
