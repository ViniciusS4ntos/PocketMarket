package com.pocketmarket.upload.service;

import com.pocketmarket.upload.dto.ImageUploadResponse;
import com.pocketmarket.upload.support.ImageUrlBuilder;
import com.pocketmarket.upload.support.UploadDirectoryResolver;
import com.pocketmarket.upload.support.UploadDirectoryManager;
import com.pocketmarket.upload.validator.UploadPathValidator;
import com.pocketmarket.upload.support.ImageFilenameGenerator;
import com.pocketmarket.upload.validator.ImageFileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final ImageFileValidator imageFileValidator;
    private final ImageFilenameGenerator imageFilenameGenerator;
    private final UploadDirectoryResolver uploadDirectoryResolver;
    private final UploadDirectoryManager uploadDirectoryManager;
    private final UploadPathValidator uploadPathValidator;
    private final LocalImageStorageService localImageStorageService;
    private final ImageUrlBuilder imageUrlBuilder;

    public ImageUploadResponse uploadImage(MultipartFile file) {
        imageFileValidator.validate(file);

        String filename = imageFilenameGenerator.generate(file.getOriginalFilename());

        Path imageDirectory = uploadDirectoryResolver.resolveImageDirectory();
        uploadDirectoryManager.createDirectoryIfNotExists(imageDirectory);

        Path destination = uploadDirectoryResolver.resolveImageDestination(filename);
        uploadPathValidator.validateDestinationInsideDirectory(destination, imageDirectory);

        localImageStorageService.store(file, destination);

        String url = imageUrlBuilder.buildImageUrl(filename);

        return new ImageUploadResponse(
                url,
                filename,
                file.getContentType(),
                file.getSize()
        );
    }
}