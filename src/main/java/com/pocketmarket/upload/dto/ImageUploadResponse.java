package com.pocketmarket.upload.dto;

public record ImageUploadResponse(
        String url,
        String filename,
        String contentType,
        long size
) {
}