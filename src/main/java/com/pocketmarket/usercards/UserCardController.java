package com.pocketmarket.usercards;

import com.pocketmarket.user.User;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
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
@RequestMapping("/api/v1/user-cards")
@RequiredArgsConstructor
@Tag(name = "User Cards", description = "Endpoints relacionados ao gerenciamento de cartas do usuário")
public class UserCardController {

    private final UserCardService userCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adicionar carta à coleção", description = "Adiciona uma nova carta à coleção do usuário autenticado")
    @ApiResponse(responseCode = "201", description = "Carta adicionada com sucesso",
            content = @Content(schema = @Schema(implementation = UserCardResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public UserCardResponse createUserCard(@AuthenticationPrincipal User currentUser, @RequestBody @Valid UserCardRequest request) {
        return userCardService.createUserCard(currentUser, request);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar minhas cartas", description = "Retorna uma lista paginada de todas as cartas na coleção do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Cartas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public Page<UserCardResponse> getMyCards(@AuthenticationPrincipal User currentUser, @Parameter(description = "Configuração de paginação") @PageableDefault(size = 20) Pageable pageable) {
        return userCardService.getMyCards(currentUser, pageable);
    }

    @GetMapping
    @Operation(summary = "Listar todas as cartas", description = "Retorna uma lista paginada de todas as cartas dos usuários no sistema")
    @ApiResponse(responseCode = "200", description = "Cartas encontradas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public Page<UserCardResponse> getAllCards(@Parameter(description = "Configuração de paginação") @PageableDefault(size = 20) Pageable pageable) {
        return userCardService.getAllCards(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar carta do usuário por ID", description = "Retorna os dados de uma carta específica da coleção de um usuário")
    @ApiResponse(responseCode = "200", description = "Carta encontrada com sucesso",
            content = @Content(schema = @Schema(implementation = UserCardResponse.class)))
    @ApiResponse(responseCode = "404", description = "Carta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public UserCardResponse getUserCard(@Parameter(description = "ID da carta do usuário") @PathVariable UUID id) {
        return userCardService.getUserCard(id);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remover carta da coleção", description = "Remove uma carta da coleção do usuário autenticado")
    @ApiResponse(responseCode = "204", description = "Carta removida com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não autorizado a deletar esta carta")
    @ApiResponse(responseCode = "404", description = "Carta não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public void deleteUserCard(@Parameter(description = "ID da carta a ser removida") @PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        userCardService.deleteUserCard(id, currentUser);
    }
}
