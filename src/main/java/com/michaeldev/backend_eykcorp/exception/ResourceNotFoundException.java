package com.michaeldev.backend_eykcorp.exception;

// EXCEPCIÓN PARA RECURSOS NO ENCONTRADOS
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}