package com.pocketmarket.upload;

import com.pocketmarket.upload.service.LocalImageStorageService;
import com.pocketmarket.upload.support.UploadDirectoryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalImageStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeCopiesFileToDestination() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "card.png", "image/png", "abc".getBytes());
        Path destination = tempDir.resolve("card.png");

        new LocalImageStorageService().store(file, destination);

        assertThat(Files.readString(destination)).isEqualTo("abc");
    }

    @Test
    void createDirectoryIfNotExistsCreatesDirectory() {
        Path directory = tempDir.resolve("uploads").resolve("images");

        new UploadDirectoryManager().createDirectoryIfNotExists(directory);

        assertThat(directory).exists().isDirectory();
    }

    @Test
    void storeWrapsIOException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("boom"));

        assertThatThrownBy(() -> new LocalImageStorageService().store(file, tempDir.resolve("card.png")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao salvar imagem.");
    }

    @Test
    void createDirectoryIfNotExistsWrapsIOException() throws Exception {
        Path existingFile = tempDir.resolve("not-a-directory");
        Files.writeString(existingFile, "content");

        assertThatThrownBy(() -> new UploadDirectoryManager().createDirectoryIfNotExists(existingFile.resolve("child")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao criar diretório de upload.");
    }
}
