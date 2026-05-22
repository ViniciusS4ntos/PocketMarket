package com.pocketmarket.upload.support;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class UploadDirectoryManager {

    public void createDirectoryIfNotExists(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new RuntimeException("Erro ao criar diretório de upload.", exception);
        }
    }
}