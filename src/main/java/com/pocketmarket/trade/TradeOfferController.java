package com.pocketmarket.trade;

import com.pocketmarket.trade.dto.CreateTradeOfferRequest;
import com.pocketmarket.trade.dto.TradeOfferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trade-offers")
@Tag(name = "Trade Offers", description = "Endpoints relacionados a propostas de troca de cartas")
public class TradeOfferController {

    private final TradeOfferService tradeOfferService;

    public TradeOfferController(
            TradeOfferService tradeOfferService
    ) {
        this.tradeOfferService = tradeOfferService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar proposta de troca", description = "Cria uma nova proposta de troca de cartas")
    @ApiResponse(responseCode = "200", description = "Proposta de troca criada com sucesso",
            content = @Content(schema = @Schema(implementation = TradeOfferResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<TradeOfferResponse> create(
            @RequestBody CreateTradeOfferRequest request
    ) {

        return ResponseEntity.ok(
                tradeOfferService.create(request)
        );
    }

    @GetMapping("/sent")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar propostas enviadas", description = "Retorna uma lista de todas as propostas de troca enviadas pelo usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Propostas enviadas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = TradeOfferResponse.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<List<TradeOfferResponse>> sent() {

        return ResponseEntity.ok(
                tradeOfferService.getSent()
        );
    }

    @GetMapping("/received")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar propostas recebidas", description = "Retorna uma lista de todas as propostas de troca recebidas pelo usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Propostas recebidas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = TradeOfferResponse.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<List<TradeOfferResponse>> received() {

        return ResponseEntity.ok(
                tradeOfferService.getReceived()
        );
    }

    @PatchMapping("/{id}/accept")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Aceitar proposta de troca", description = "Aceita uma proposta de troca de cartas")
    @ApiResponse(responseCode = "200", description = "Proposta aceita com sucesso",
            content = @Content(schema = @Schema(implementation = TradeOfferResponse.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não autorizado a aceitar esta proposta")
    @ApiResponse(responseCode = "404", description = "Proposta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<TradeOfferResponse> accept(
            @Parameter(description = "ID da proposta de troca")
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                tradeOfferService.accept(id)
        );
    }
}
