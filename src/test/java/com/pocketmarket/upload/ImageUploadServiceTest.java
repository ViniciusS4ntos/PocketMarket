package com.pocketmarket.upload;

import com.pocketmarket.upload.dto.ImageUploadResponse;
import com.pocketmarket.upload.service.ImageUploadService;
import com.pocketmarket.upload.service.LocalImageStorageService;
import com.pocketmarket.upload.support.ImageFilenameGenerator;
import com.pocketmarket.upload.support.ImageUrlBuilder;
import com.pocketmarket.upload.support.UploadDirectoryManager;
import com.pocketmarket.upload.support.UploadDirectoryResolver;
import com.pocketmarket.upload.validator.ImageFileValidator;
import com.pocketmarket.upload.validator.UploadPathValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageUploadServiceTest {

    @Mock
    private ImageFileValidator imageFileValidator;

    @Mock
    private ImageFilenameGenerator imageFilenameGenerator;

    @Mock
    private UploadDirectoryResolver uploadDirectoryResolver;

    @Mock
    private UploadDirectoryManager uploadDirectoryManager;

    @Mock
    private UploadPathValidator uploadPathValidator;

    @Mock
    private LocalImageStorageService localImageStorageService;

    @Mock
    private ImageUrlBuilder imageUrlBuilder;

    @InjectMocks
    private ImageUploadService imageUploadService;

    @Test
    void uploadImageValidatesStoresAndReturnsResponse() {
        MockMultipartFile file = new MockMultipartFile("file", "card.png", "image/png", "abc".getBytes());
        Path directory = Path.of("/uploads/images");
        Path destination = directory.resolve("generated.png");

        when(imageFilenameGenerator.generate(file.getOriginalFilename())).thenReturn("generated.png");
        when(uploadDirectoryResolver.resolveImageDirectory()).thenReturn(directory);
        when(uploadDirectoryResolver.resolveImageDestination("generated.png")).thenReturn(destination);
        when(imageUrlBuilder.buildImageUrl("generated.png")).thenReturn("/uploads/images/generated.png");

        ImageUploadResponse response = imageUploadService.uploadImage(file);

        assertThat(response.filename()).isEqualTo("generated.png");
        assertThat(response.url()).isEqualTo("/uploads/images/generated.png");
        verify(imageFileValidator).validate(file);
        verify(uploadDirectoryManager).createDirectoryIfNotExists(directory);
        verify(uploadPathValidator).validateDestinationInsideDirectory(destination, directory);
        verify(localImageStorageService).store(file, destination);
    }
}
