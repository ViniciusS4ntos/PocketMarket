package com.pocketmarket.purchase;

import com.pocketmarket.enums.ListingStatus;
import com.pocketmarket.enums.ListingType;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.exceptions.BusinessException;
import com.pocketmarket.exceptions.ForbiddenException;
import com.pocketmarket.listing.Listing;
import com.pocketmarket.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuyListingValidator {

    public void validate(User user, Listing listing) {

        if (listing.getListingType() != ListingType.SALE) {
            throw new BusinessException("Apenas anúncios de venda direta podem ser comprados.");
        }

        if (listing.getListingStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("Este anúncio não está ativo.");
        }

        if (listing.getPrice() == null || listing.getPrice() <= 0) {
            throw new BusinessException("Este anúncio não possui preço válido.");
        }

        if (listing.getSeller().getId().equals(user.getId())) {
            throw new ForbiddenException("O vendedor não pode comprar o próprio anúncio.");
        }

        if (user.getCredits() < listing.getPrice()) {
            throw new BusinessException("Créditos insuficientes para realizar a compra.");
        }

        if (listing.getUserCard().getStatus() != UserCardStatus.LISTED) {
            throw new BusinessException("A carta não está disponível para compra.");
        }
    }
}