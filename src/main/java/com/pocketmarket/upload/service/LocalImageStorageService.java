package com.pocketmarket.upload.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class LocalImageStorageService {

    public void store(MultipartFile file, Path destination) {
        try {
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException exception) {
            throw new RuntimeException("Erro ao salvar imagem.", exception);
        }
    }
}