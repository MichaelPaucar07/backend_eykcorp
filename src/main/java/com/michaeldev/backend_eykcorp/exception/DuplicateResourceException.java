package com.michaeldev.backend_eykcorp.exception;

// EXCEPCIÓN PARA RECURSOS DUPLICADOS
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}