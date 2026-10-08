package com.michaeldev.backend_eykcorp.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

// ESTRUCTURA ÚNICA DE RESPUESTA PARA TODA LA API (ÉXITO Y ERROR)
public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        Map<String, String> errors,
        LocalDateTime timestamp) {

    // RESPUESTA EXITOSA CON DATOS
    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(true, status.value(), message, data, null, LocalDateTime.now());
    }

    // RESPUESTA DE ERROR SIN DETALLE POR CAMPO
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(false, status.value(), message, null, null, LocalDateTime.now());
    }

    // RESPUESTA DE ERROR CON DETALLE POR CAMPO (VALIDACIONES)
    public static <T> ApiResponse<T> error(HttpStatus status, String message, Map<String, String> errors) {
        return new ApiResponse<>(false, status.value(), message, null, errors, LocalDateTime.now());
    }
}
