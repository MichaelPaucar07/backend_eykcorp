package com.michaeldev.backend_eykcorp.exception;

import com.michaeldev.backend_eykcorp.dto.ApiResponse;
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

    // METODO PARA MANEJAR TODAS LAS EXCEPCIONES DE NEGOCIO (404, 409, ...)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("Error de negocio en {}: {}", request.getRequestURI(), ex.getMessage());
        return build(ex.getStatus(), ex.getMessage());
    }

    // Respaldo de la restricción UNIQUE de la base de datos (peticiones
    // concurrentes)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.warn("Violación de integridad de datos en {}: {}", request.getRequestURI(),
                ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "El registro viola una restricción de datos (posible correo duplicado)");
    }

    // METODO PARA MANEJAR LA EXCEPCIÓN MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        log.warn("Errores de validación en {}: {}", request.getRequestURI(), errors);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiResponse.error(status, "La solicitud contiene datos inválidos", errors));
    }

    // METODO PARA MANEJAR LA EXCEPCIÓN HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        log.warn("Cuerpo de la solicitud inválido en {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud es inválido o está mal formado");
    }

    // METODO PARA MANEJAR LA EXCEPCIÓN MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        log.warn("Parámetro inválido '{}' en {}: {}", ex.getName(), request.getRequestURI(), ex.getValue());
        return build(HttpStatus.BAD_REQUEST, "El parámetro '" + ex.getName() + "' tiene un valor inválido");
    }

    // METODO PARA MANEJAR LA EXCEPCIÓN HttpRequestMethodNotSupportedException
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "El método " + ex.getMethod() + " no está permitido en esta ruta");
    }

    // METODO PARA MANEJAR LA EXCEPCIÓN NoResourceFoundException
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "La ruta solicitada no existe");
    }

    // METODO PARA MANEJAR CUALQUIER OTRA EXCEPCIÓN NO CONTROLADA
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor");
    }

    // MÉTODO PRIVADO PARA CONSTRUIR UNA RESPUESTA DE ERROR
    private ResponseEntity<ApiResponse<Void>> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.error(status, message));
    }
}
