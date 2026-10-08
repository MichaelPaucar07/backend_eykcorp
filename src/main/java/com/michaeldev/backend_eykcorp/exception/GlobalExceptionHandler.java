package com.michaeldev.backend_eykcorp.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
// CLASE DE MANEJO GLOBAL DE EXCEPCIONES PARA LA API
public class GlobalExceptionHandler {
    // METODO PARA MANEJAR LA EXCEPCIÓN ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }
    // METODO PARA MANEJAR LA EXCEPCIÓN DuplicateResourceException
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Recurso duplicado: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // Respaldo de la restricción UNIQUE de la base de datos (peticiones
    // concurrentes)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.warn("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "El registro viola una restricción de datos (posible correo duplicado)",
                request);
    }
    // METODO PARA MANEJAR LA EXCEPCIÓN MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        log.warn("Errores de validación en {}: {}", request.getRequestURI(), errors);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), status.getReasonPhrase(),
                        "La solicitud contiene datos inválidos", request.getRequestURI(), errors));
    }
    // METODO PARA MANEJAR LA EXCEPCIÓN HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        log.warn("Cuerpo de la solicitud inválido: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud es inválido o está mal formado", request);
    }
    // METODO PARA MANEJAR LA EXCEPCIÓN MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        log.warn("Parámetro inválido '{}': {}", ex.getName(), ex.getValue());
        return build(HttpStatus.BAD_REQUEST, "El parámetro '" + ex.getName() + "' tiene un valor inválido", request);
    }
    // METODO PARA MANEJAR LA EXCEPCIÓN HttpRequestMethodNotSupportedException
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request);
    }
    // METODO PARA MANEJAR LA EXCEPCIÓN NoResourceFoundException
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", request);
    }
    // METODO PARA MANEJAR CUALQUIER OTRA EXCEPCIÓN NO CONTROLADA
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor", request);
    }
    // MÉTODO PRIVADO PARA CONSTRUIR UNA RESPUESTA DE ERROR
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
    }
}