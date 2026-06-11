package com.pocketmarket.upload.controller;

import com.pocketmarket.upload.service.ImageUploadService;
import com.pocketmarket.upload.dto.ImageUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@Tag(name = "Uploads", description = "Endpoints relacionados ao upload de arquivos e imagens")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de imagem", description = "Realiza o upload de uma imagem e retorna a URL para acesso")
    @ApiResponse(responseCode = "200", description = "Imagem enviada com sucesso",
            content = @Content(schema = @Schema(implementation = ImageUploadResponse.class)))
    @ApiResponse(responseCode = "400", description = "Arquivo inválido ou formato não suportado")
    @ApiResponse(responseCode = "413", description = "Arquivo muito grande")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @Parameter(description = "Arquivo de imagem a ser enviado", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(imageUploadService.uploadImage(file));
    }
}
