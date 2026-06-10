package com.pocketmarket.cards;

import com.pocketmarket.cards.dto.CardResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Endpoints relacionados ao gerenciamento de cartas do jogo")
public class CardController {

    private final CardService cardService;

    @GetMapping
    @Operation(summary = "Listar todas as cartas", description = "Retorna uma lista de todas as cartas disponíveis no sistema")
    @ApiResponse(responseCode = "200", description = "Cartas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = CardResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public List<CardResponseDTO> findAllCards() {
        return cardService.findAllCards();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar carta por ID", description = "Retorna os dados de uma carta específica")
    @ApiResponse(responseCode = "200", description = "Carta encontrada com sucesso",
            content = @Content(schema = @Schema(implementation = CardResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Carta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public CardResponseDTO findCard(@Parameter(description = "ID da carta") @PathVariable UUID id) {
        return cardService.findCard(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma carta", description = "Remove uma carta do sistema")
    @ApiResponse(responseCode = "204", description = "Carta deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Carta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public void delete(@Parameter(description = "ID da carta a ser deletada") @PathVariable UUID id) {
        cardService.delete(id);
    }
}
