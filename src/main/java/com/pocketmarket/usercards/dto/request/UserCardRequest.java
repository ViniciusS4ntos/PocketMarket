package com.pocketmarket.usercards.dto.request;

import com.pocketmarket.enums.CardCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCardRequest(
        @NotBlank
        String externalCardId,

        @NotNull
        CardCondition condition,

        @NotBlank
        String proofImageUrl
) {
}
