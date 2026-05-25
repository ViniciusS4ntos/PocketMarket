package com.pocketmarket.upload.support;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class ImageFilenameGenerator {

    public String generate(String originalFilename) {
        String cleanedFilename = StringUtils.cleanPath(originalFilename);

        String extension = extractExtension(cleanedFilename);

        return UUID.randomUUID() + extension;
    }

    private String extractExtension(String filename) {
        int extensionIndex = filename.lastIndexOf(".");

        if (extensionIndex == -1) {
            throw new IllegalArgumentException("Arquivo sem extensão.");
        }

        return filename.substring(extensionIndex).toLowerCase();
    }
}