package com.pocketmarket.collection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository extends JpaRepository<CollectionCard, UUID> {

    List<CollectionCard> findAllByUserId(UUID userId);

    Optional<CollectionCard> findByUserIdAndUserCardId(UUID userId, UUID userCardId);
}