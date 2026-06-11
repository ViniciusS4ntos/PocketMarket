package com.pocketmarket.user;

import com.pocketmarket.user.dtos.in.UserCreditsRequest;
import com.pocketmarket.user.dtos.in.UserDTORequest;
import com.pocketmarket.user.dtos.out.UserCreditsResponse;
import com.pocketmarket.user.dtos.out.UserDTOResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints relacionados ao gerenciamento de perfil de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = UserDTOResponse.class)))
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<UserDTOResponse> buscarUsuarioPorId(@Parameter(description = "ID do usuário") @PathVariable UUID id){
        return ResponseEntity.ok(
                mapper.map(userService.buscarPerfilPorId(id), UserDTOResponse.class)
        );
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar perfil do usuário", description = "Atualiza os dados de perfil de um usuário")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = UserDTOResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<UserDTOResponse> atualizarPerfil(@Parameter(description = "ID do usuário a ser atualizado") @PathVariable UUID id, @RequestBody UserDTORequest user){
        User entity = mapper.map(user, User.class);
        return ResponseEntity.ok(
                mapper.map(userService.atualizarPerfil(id, entity), UserDTOResponse.class)
        );
    }

    @GetMapping("/me/credits")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obter créditos do usuário atual", description = "Retorna o saldo de créditos do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Créditos retornados com sucesso",
            content = @Content(schema = @Schema(implementation = UserCreditsResponse.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<UserCreditsResponse> myCredits(@AuthenticationPrincipal User currentUser){
        return userService.myCredits(currentUser);
    }

    @PatchMapping("/credit/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adicionar créditos a um usuário", description = "Adiciona créditos a uma conta de usuário")
    @ApiResponse(responseCode = "200", description = "Créditos adicionados com sucesso",
            content = @Content(schema = @Schema(implementation = UserCreditsResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public UserCreditsResponse addCredits(@Parameter(description = "ID do usuário") @PathVariable UUID userId, @RequestBody @Valid UserCreditsRequest request) {
        return userService.addCredits(userId, request);
    }
}
