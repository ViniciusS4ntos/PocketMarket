package com.pocketmarket.upload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.upload")
public record StorageProperties(
        String baseDir,
        String imageDir,
        String publicPath
) {
}