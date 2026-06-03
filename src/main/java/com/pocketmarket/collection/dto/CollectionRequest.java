package com.pocketmarket.collection.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CollectionRequest(

        @NotNull(message = "userCardId is required")
        UUID userCardId
) {}