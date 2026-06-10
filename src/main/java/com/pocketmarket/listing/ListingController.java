package com.pocketmarket.listing;

import com.pocketmarket.listing.dto.request.ListingAuctionRequest;
import com.pocketmarket.listing.dto.request.ListingSaleRequest;
import com.pocketmarket.listing.dto.response.ListingAuctionResponse;
import com.pocketmarket.listing.dto.response.ListingResponse;
import com.pocketmarket.listing.dto.response.ListingSaleResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
@Tag(name = "Listings", description = "Endpoints relacionados à listagem de cartas para venda ou leilão")
public class ListingController {

    private final ListingService listingService;

    @PostMapping("/sale/{userCardId}")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar listagem de venda", description = "Cria uma nova listagem para vender uma carta por preço fixo")
    @ApiResponse(responseCode = "201", description = "Listagem de venda criada com sucesso",
            content = @Content(schema = @Schema(implementation = ListingSaleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Carta do usuário não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ListingSaleResponse postListingSale(@AuthenticationPrincipal User currentUser, @Parameter(description = "ID da carta do usuário a ser vendida") @PathVariable UUID userCardId, @RequestBody @Valid ListingSaleRequest request) {
        return listingService.postListingSale(currentUser, userCardId, request);
    }

    @PostMapping("/auction/{userCardId}")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar listagem de leilão", description = "Cria uma nova listagem para colocar uma carta em leilão")
    @ApiResponse(responseCode = "201", description = "Listagem de leilão criada com sucesso",
            content = @Content(schema = @Schema(implementation = ListingAuctionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Carta do usuário não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ListingAuctionResponse postListingAuction(@AuthenticationPrincipal User currentUser, @Parameter(description = "ID da carta do usuário a ser colocada em leilão") @PathVariable UUID userCardId, @RequestBody @Valid ListingAuctionRequest request) {
        return listingService.postListingAuction(currentUser, userCardId, request);
    }

    @GetMapping
    @Operation(summary = "Listar todas as listagens", description = "Retorna uma lista paginada de todas as listagens disponíveis")
    @ApiResponse(responseCode = "200", description = "Listagens encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public Page<ListingResponse> showListings(@Parameter(description = "Configuração de paginação") @PageableDefault(size = 20) Pageable pageable) {
        return listingService.showListings(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar listagem por ID", description = "Retorna os detalhes de uma listagem específica")
    @ApiResponse(responseCode = "200", description = "Listagem encontrada com sucesso",
            content = @Content(schema = @Schema(implementation = ListingResponse.class)))
    @ApiResponse(responseCode = "404", description = "Listagem não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ListingResponse showListing(@Parameter(description = "ID da listagem") @PathVariable UUID id) {
        return listingService.showListing(id);
    }

    @PatchMapping("/{id}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancelar listagem", description = "Cancela uma listagem de venda ou leilão")
    @ApiResponse(responseCode = "200", description = "Listagem cancelada com sucesso",
            content = @Content(schema = @Schema(implementation = ListingResponse.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não autorizado a cancelar esta listagem")
    @ApiResponse(responseCode = "404", description = "Listagem não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ListingResponse cancelListing(@AuthenticationPrincipal User currentUser, @Parameter(description = "ID da listagem a ser cancelada") @PathVariable UUID id) {
        return listingService.cancelListing(currentUser, id);
    }
}
