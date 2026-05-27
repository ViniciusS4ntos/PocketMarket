package com.pocketmarket.favorite;

import com.pocketmarket.favorite.dto.FavoriteResponse;
import com.pocketmarket.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{cardId}")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @PathVariable UUID cardId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(201).body(favoriteService.addFavorite(cardId, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> listFavorites(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(favoriteService.listFavorites(currentUser));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable UUID cardId,
            @AuthenticationPrincipal User currentUser) {

        favoriteService.removeFavorite(cardId, currentUser);
        return ResponseEntity.noContent().build();
    }
}