package com.pocketmarket.upload.support;

import com.pocketmarket.upload.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUrlBuilder {

    private final StorageProperties storageProperties;

    public String buildImageUrl(String filename) {
        return storageProperties.publicPath()
                + "/"
                + storageProperties.imageDir()
                + "/"
                + filename;
    }
}