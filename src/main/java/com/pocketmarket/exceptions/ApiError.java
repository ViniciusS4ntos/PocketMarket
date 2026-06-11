package com.pocketmarket.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ApiError(
        int status,
        String error,
        String message,
        String path,

        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
        LocalDateTime timestamp
) {

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(status, error, message, path, LocalDateTime.now());
    }
}