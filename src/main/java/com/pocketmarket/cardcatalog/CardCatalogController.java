package com.pocketmarket.cardcatalog;

import com.pocketmarket.cardcatalog.dto.CardCatalogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card-catalog")
@RequiredArgsConstructor
@Validated
@Tag(name = "Card Catalog", description = "Endpoints relacionados ao catálogo de cartas disponíveis")
public class CardCatalogController {

    private final CardCatalogService cardCatalogService;

    @GetMapping
    @Operation(summary = "Listar catálogo de cartas", description = "Retorna uma lista paginada de todas as cartas disponíveis no catálogo")
    @ApiResponse(responseCode = "200", description = "Cartas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public Page<CardCatalogResponse> findCards(@Parameter(description = "Configuração de paginação") @PageableDefault(size = 20) Pageable pageable) {
        return cardCatalogService.findCards(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar cartas por nome", description = "Busca cartas no catálogo pelo nome")
    @ApiResponse(responseCode = "200", description = "Cartas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = CardCatalogResponse.class)))
    @ApiResponse(responseCode = "400", description = "Parâmetro de busca inválido")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public List<CardCatalogResponse> searchByName(@Parameter(description = "Nome da carta a buscar") @RequestParam String name) {
        return cardCatalogService.searchByName(name);
    }

    @GetMapping("/{externalId}")
    @Operation(summary = "Buscar carta por ID externo", description = "Retorna os dados de uma carta específica do catálogo pelo seu ID externo")
    @ApiResponse(responseCode = "200", description = "Carta encontrada com sucesso",
            content = @Content(schema = @Schema(implementation = CardCatalogResponse.class)))
    @ApiResponse(responseCode = "404", description = "Carta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public CardCatalogResponse findByExternalId(@Parameter(description = "ID externo da carta") @PathVariable @NotBlank String externalId) {
        return cardCatalogService.findByExternalId(externalId);
    }
}
