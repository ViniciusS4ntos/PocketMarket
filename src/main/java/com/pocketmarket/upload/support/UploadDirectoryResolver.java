package com.pocketmarket.upload.support;

import com.pocketmarket.upload.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class UploadDirectoryResolver {

    private final StorageProperties storageProperties;

    public Path resolveImageDirectory() {
        return Paths.get(
                storageProperties.baseDir(),
                storageProperties.imageDir()
        ).toAbsolutePath().normalize();
    }

    public Path resolveImageDestination(String filename) {
        return resolveImageDirectory()
                .resolve(filename)
                .normalize();
    }
}