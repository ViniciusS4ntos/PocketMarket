package com.pocketmarket.auction;

import com.pocketmarket.auction.dto.request.AuctionBidRequest;
import com.pocketmarket.auction.dto.response.AuctionBidResponse;
import com.pocketmarket.auction.dto.response.AuctionFinishResponse;
import com.pocketmarket.auction.service.AuctionBidService;
import com.pocketmarket.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
@Tag(name = "Auctions", description = "Endpoints relacionados a leilões e lances em cartas")
public class AuctionBidController {

    private final AuctionBidService auctionBidService;

    @PostMapping("/{listingId}/bids")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar lance no leilão", description = "Realiza um novo lance em um leilão de carta")
    @ApiResponse(responseCode = "201", description = "Lance criado com sucesso",
            content = @Content(schema = @Schema(implementation = AuctionBidResponse.class)))
    @ApiResponse(responseCode = "400", description = "Operação inválida")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Leilão não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public AuctionBidResponse createBid(
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "ID da listagem de leilão")
            @PathVariable UUID listingId,
            @RequestBody AuctionBidRequest request) {
        return auctionBidService.createBid(currentUser, listingId, request);
    }

    @GetMapping("/{listingId}/bids")
    @Operation(summary = "Listar lances de um leilão", description = "Retorna uma lista paginada de todos os lances realizados em um leilão específico")
    @ApiResponse(responseCode = "200", description = "Lances encontrados com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "404", description = "Leilão não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public Page<AuctionBidResponse> getBids(
            @Parameter(description = "ID da listagem de leilão")
            @PathVariable UUID listingId,
            @Parameter(description = "Configuração de paginação")
            @PageableDefault Pageable pageable) {
        return auctionBidService.getBids(listingId, pageable);
    }

    @PostMapping("/{listingId}/finish")
    @Operation(summary = "Finalizar leilão", description = "Finaliza um leilão e determina o vencedor baseado no lance mais alto")
    @ApiResponse(responseCode = "200", description = "Leilão finalizado com sucesso",
            content = @Content(schema = @Schema(implementation = AuctionFinishResponse.class)))
    @ApiResponse(responseCode = "400", description = "Leilão já foi finalizado")
    @ApiResponse(responseCode = "404", description = "Leilão não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public AuctionFinishResponse finishAuction(
            @Parameter(description = "ID da listagem de leilão a ser finalizada")
            @PathVariable UUID listingId) {
        return auctionBidService.finishAuction(listingId);
    }
}
