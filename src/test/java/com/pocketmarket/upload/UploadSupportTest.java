package com.pocketmarket.upload;

import com.pocketmarket.upload.config.StorageProperties;
import com.pocketmarket.upload.support.ImageFilenameGenerator;
import com.pocketmarket.upload.support.ImageUrlBuilder;
import com.pocketmarket.upload.support.UploadDirectoryResolver;
import com.pocketmarket.upload.validator.ImageFileValidator;
import com.pocketmarket.upload.validator.UploadPathValidator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadSupportTest {

    @Test
    void imageFileValidatorAcceptsValidImage() {
        ImageFileValidator validator = new ImageFileValidator();
        MockMultipartFile file = new MockMultipartFile("file", "card.png", "image/png", "abc".getBytes());

        validator.validate(file);
    }

    @Test
    void imageFileValidatorRejectsInvalidFiles() {
        ImageFileValidator validator = new ImageFileValidator();

        assertThatThrownBy(() -> validator.validate(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> validator.validate(new MockMultipartFile("file", "card.txt", "text/plain", "abc".getBytes())))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> validator.validate(new MockMultipartFile("file", "card", "image/png", "abc".getBytes())))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> validator.validate(new MockMultipartFile("file", "", "image/png", "abc".getBytes())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void filenameGeneratorPreservesLowercaseExtension() {
        String filename = new ImageFilenameGenerator().generate("Card.PNG");

        assertThat(filename).endsWith(".png");
    }

    @Test
    void filenameGeneratorRejectsFileWithoutExtension() {
        assertThatThrownBy(() -> new ImageFilenameGenerator().generate("card"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arquivo sem extensão.");
    }

    @Test
    void uploadPathValidatorRejectsDestinationOutsideDirectory() {
        UploadPathValidator validator = new UploadPathValidator();

        validator.validateDestinationInsideDirectory(Path.of("/tmp/uploads/card.png"), Path.of("/tmp/uploads"));

        assertThatThrownBy(() -> validator.validateDestinationInsideDirectory(Path.of("/tmp/other/card.png"), Path.of("/tmp/uploads")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void urlBuilderAndDirectoryResolverUseStorageProperties() {
        StorageProperties properties = new StorageProperties("uploads", "images", "/uploads");
        ImageUrlBuilder urlBuilder = new ImageUrlBuilder(properties);
        UploadDirectoryResolver resolver = new UploadDirectoryResolver(properties);

        assertThat(urlBuilder.buildImageUrl("card.png")).isEqualTo("/uploads/images/card.png");
        assertThat(resolver.resolveImageDirectory().toString()).endsWith("uploads/images");
        assertThat(resolver.resolveImageDestination("card.png").toString()).endsWith("uploads/images/card.png");
    }
}
