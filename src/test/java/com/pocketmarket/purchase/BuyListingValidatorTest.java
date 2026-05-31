package com.pocketmarket.purchase;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCard;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuyListingValidatorTest {

    private final BuyListingValidator validator = new BuyListingValidator();

    @Test
    void validateAcceptsValidSaleListing() {
        User buyer = user(1000L);
        Listing listing = saleListing(user(0L), 500L, ListingStatus.ACTIVE, UserCardStatus.LISTED);

        assertThatCode(() -> validator.validate(buyer, listing)).doesNotThrowAnyException();
    }

    @Test
    void validateRejectsInvalidListingTypeStatusAndPrice() {
        User buyer = user(1000L);

        Listing auction = saleListing(user(0L), 500L, ListingStatus.ACTIVE, UserCardStatus.LISTED);
        auction.setListingType(ListingType.AUCTION);
        assertThatThrownBy(() -> validator.validate(buyer, auction))
                .hasMessage("Apenas anúncios de venda direta podem ser comprados.");

        Listing canceled = saleListing(user(0L), 500L, ListingStatus.CANCELED, UserCardStatus.LISTED);
        assertThatThrownBy(() -> validator.validate(buyer, canceled))
                .hasMessage("Este anúncio não está ativo.");

        Listing withoutPrice = saleListing(user(0L), null, ListingStatus.ACTIVE, UserCardStatus.LISTED);
        assertThatThrownBy(() -> validator.validate(buyer, withoutPrice))
                .hasMessage("Este anúncio não possui preço válido.");

        Listing zeroPrice = saleListing(user(0L), 0L, ListingStatus.ACTIVE, UserCardStatus.LISTED);
        assertThatThrownBy(() -> validator.validate(buyer, zeroPrice))
                .hasMessage("Este anúncio não possui preço válido.");
    }

    @Test
    void validateRejectsSellerInsufficientCreditsAndUnavailableCard() {
        User seller = user(0L);
        Listing listing = saleListing(seller, 500L, ListingStatus.ACTIVE, UserCardStatus.LISTED);

        assertThatThrownBy(() -> validator.validate(seller, listing))
                .hasMessage("O vendedor não pode comprar o próprio anúncio.");

        assertThatThrownBy(() -> validator.validate(user(100L), listing))
                .hasMessage("Créditos insuficientes para realizar a compra.");

        Listing unavailable = saleListing(seller, 500L, ListingStatus.ACTIVE, UserCardStatus.AVAILABLE);
        assertThatThrownBy(() -> validator.validate(user(1000L), unavailable))
                .hasMessage("A carta não está disponível para compra.");
    }

    private Listing saleListing(User seller, Long price, ListingStatus status, UserCardStatus userCardStatus) {
        return Listing.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .userCard(UserCard.builder().id(UUID.randomUUID()).owner(seller).status(userCardStatus).build())
                .listingType(ListingType.SALE)
                .listingStatus(status)
                .price(price)
                .build();
    }

    private User user(Long credits) {
        return User.builder().id(UUID.randomUUID()).name("Ash").credits(credits).build();
    }
}
