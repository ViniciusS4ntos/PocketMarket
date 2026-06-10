package com.pocketmarket.purchase;

import com.pocketmarket.purchase.dto.response.PurchaseResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "Endpoints relacionados a compras e vendas de cartas")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping("/my-purchases")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar minhas compras", description = "Retorna uma lista paginada de todas as cartas compradas pelo usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Compras encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public Page<PurchaseResponse> listMyPurchases(@AuthenticationPrincipal User user, @Parameter(description = "Configuração de paginação") @PageableDefault(size = 10) Pageable pageable) {
        return purchaseService.listMyPurchases(user, pageable);
    }

    @GetMapping("/my-sales")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar minhas vendas", description = "Retorna uma lista paginada de todas as cartas vendidas pelo usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Vendas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public Page<PurchaseResponse> listMySales(@AuthenticationPrincipal User user, @Parameter(description = "Configuração de paginação") @PageableDefault(size = 10) Pageable pageable) {
        return purchaseService.listMySales(user, pageable);
    }

    @PostMapping("/{listingId}/buy")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Comprar uma listagem", description = "Realiza a compra de uma carta de uma listagem específica")
    @ApiResponse(responseCode = "200", description = "Compra realizada com sucesso",
            content = @Content(schema = @Schema(implementation = PurchaseResponse.class)))
    @ApiResponse(responseCode = "400", description = "Operação inválida")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Listagem não encontrada")
    @ApiResponse(responseCode = "409", description = "Listagem indisponível para compra")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public PurchaseResponse buyListing(@AuthenticationPrincipal User user, @Parameter(description = "ID da listagem a ser comprada") @PathVariable UUID listingId) {
        return purchaseService.buyListing(user, listingId);
    }
}
