package com.pocketmarket.collection;

import com.pocketmarket.collection.dto.CollectionRequest;
import com.pocketmarket.collection.dto.CollectionResponse;
import com.pocketmarket.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Collection", description = "Endpoints relacionados à coleção de cartas do usuário")
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adicionar à coleção", description = "Adiciona uma carta ao catálogo de coleção do usuário autenticado")
    @ApiResponse(responseCode = "201", description = "Carta adicionada à coleção com sucesso",
            content = @Content(schema = @Schema(implementation = CollectionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<CollectionResponse> addToCollection(
            @Valid @RequestBody CollectionRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(201).body(collectionService.addToCollection(request, currentUser));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar coleção", description = "Retorna uma lista de todas as cartas na coleção do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Coleção retornada com sucesso",
            content = @Content(schema = @Schema(implementation = CollectionResponse.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<List<CollectionResponse>> listCollection(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(collectionService.listCollection(currentUser));
    }

    @DeleteMapping("/{userCardId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remover da coleção", description = "Remove uma carta da coleção do usuário autenticado")
    @ApiResponse(responseCode = "204", description = "Carta removida da coleção com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não autorizado a remover esta carta")
    @ApiResponse(responseCode = "404", description = "Carta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<Void> removeFromCollection(
            @Parameter(description = "ID da carta a ser removida da coleção")
            @PathVariable UUID userCardId,
            @AuthenticationPrincipal User currentUser) {

        collectionService.removeFromCollection(userCardId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
