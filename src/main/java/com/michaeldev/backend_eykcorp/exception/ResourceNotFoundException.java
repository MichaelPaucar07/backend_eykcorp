package com.michaeldev.backend_eykcorp.exception;

import org.springframework.http.HttpStatus;

// EXCEPCIÓN PARA RECURSOS NO ENCONTRADOS (404)
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
