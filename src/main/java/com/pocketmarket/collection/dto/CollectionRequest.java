package com.pocketmarket.collection.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CollectionRequest(

        @NotNull(message = "cardId is required")
        UUID cardId,

        @Min(value = 1, message = "quantity must be at least 1")
        int quantity
) {}