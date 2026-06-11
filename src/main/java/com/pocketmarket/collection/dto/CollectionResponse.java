package com.pocketmarket.collection.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CollectionResponse(
        UUID collectionId,
        UUID userCardId,
        LocalDateTime addedAt
) {}