package com.pocketmarket.favorite;

import com.pocketmarket.favorite.dto.FavoriteResponse;
import com.pocketmarket.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Endpoints relacionados ao gerenciamento de cartas favoritas")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{cardId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adicionar carta aos favoritos", description = "Marca uma carta como favorita para o usuário autenticado")
    @ApiResponse(responseCode = "201", description = "Carta adicionada aos favoritos com sucesso",
            content = @Content(schema = @Schema(implementation = FavoriteResponse.class)))
    @ApiResponse(responseCode = "400", description = "Operação inválida")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Carta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @Parameter(description = "ID da carta a ser adicionada aos favoritos")
            @PathVariable UUID cardId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(201).body(favoriteService.addFavorite(cardId, currentUser));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar cartas favoritas", description = "Retorna uma lista de todas as cartas marcadas como favoritas pelo usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Cartas favoritas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = FavoriteResponse.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<List<FavoriteResponse>> listFavorites(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(favoriteService.listFavorites(currentUser));
    }

    @DeleteMapping("/{cardId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remover carta dos favoritos", description = "Remove uma carta dos favoritos do usuário autenticado")
    @ApiResponse(responseCode = "204", description = "Carta removida dos favoritos com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Carta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<Void> removeFavorite(
            @Parameter(description = "ID da carta a ser removida dos favoritos")
            @PathVariable UUID cardId,
            @AuthenticationPrincipal User currentUser) {

        favoriteService.removeFavorite(cardId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
