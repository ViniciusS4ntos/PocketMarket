package com.pocketmarket.upload.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class ImageFileValidator {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Imagem é obrigatória.");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Formato de imagem inválido. Use PNG, JPG ou WEBP.");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new IllegalArgumentException("Nome do arquivo é obrigatório.");
        }

        if (!file.getOriginalFilename().contains(".")) {
            throw new IllegalArgumentException("Arquivo sem extensão.");
        }
    }
}