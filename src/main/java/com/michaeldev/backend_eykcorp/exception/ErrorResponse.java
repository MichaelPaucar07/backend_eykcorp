package com.michaeldev.backend_eykcorp.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

// CLASE DE RESPUESTA DE ERROR PARA LA API
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errors) {
    // MÉTODO ESTÁTICO PARA CREAR UNA RESPUESTA DE ERROR SIN DETALLES DE ERRORES       
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }
    // MÉTODO ESTÁTICO PARA CREAR UNA RESPUESTA DE ERROR CON DETALLES DE ERRORES
    public static ErrorResponse of(int status, String error, String message, String path, Map<String, String> errors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, errors);
    }
}