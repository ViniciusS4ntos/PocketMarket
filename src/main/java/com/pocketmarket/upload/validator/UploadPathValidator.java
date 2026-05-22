package com.pocketmarket.upload.validator;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class UploadPathValidator {

    public void validateDestinationInsideDirectory(Path destination, Path allowedDirectory) {
        if (!destination.startsWith(allowedDirectory)) {
            throw new IllegalArgumentException("Caminho de arquivo inválido.");
        }
    }
}