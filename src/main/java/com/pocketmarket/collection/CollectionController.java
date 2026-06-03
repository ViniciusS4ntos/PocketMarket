package com.pocketmarket.collection;

import com.pocketmarket.collection.dto.CollectionRequest;
import com.pocketmarket.collection.dto.CollectionResponse;
import com.pocketmarket.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<CollectionResponse> addToCollection(
            @Valid @RequestBody CollectionRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(201).body(collectionService.addToCollection(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<CollectionResponse>> listCollection(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(collectionService.listCollection(currentUser));
    }

    @DeleteMapping("/{userCardId}")
    public ResponseEntity<Void> removeFromCollection(
            @PathVariable UUID userCardId,
            @AuthenticationPrincipal User currentUser) {

        collectionService.removeFromCollection(userCardId, currentUser);
        return ResponseEntity.noContent().build();
    }
}