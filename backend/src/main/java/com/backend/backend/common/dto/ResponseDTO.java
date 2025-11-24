package com.backend.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ResponseDTO<T> success(T data) {
        return ResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return ResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseDTO<T> error(String message) {
        return ResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
