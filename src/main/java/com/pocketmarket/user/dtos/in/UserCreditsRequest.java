package com.pocketmarket.user.dtos.in;

import jakarta.validation.constraints.NotNull;

public record UserCreditsRequest(

        @NotNull
        Long credits
) {
}
