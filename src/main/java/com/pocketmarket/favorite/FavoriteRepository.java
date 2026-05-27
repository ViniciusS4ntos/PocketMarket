package com.pocketmarket.favorite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    List<Favorite> findAllByUserId(Integer userId);

    Optional<Favorite> findByUserIdAndCardId(Integer userId, UUID cardId);

    boolean existsByUserIdAndCardId(Integer userId, UUID cardId);
}