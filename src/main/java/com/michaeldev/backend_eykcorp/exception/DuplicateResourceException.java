package com.michaeldev.backend_eykcorp.exception;

import org.springframework.http.HttpStatus;

// EXCEPCIÓN PARA RECURSOS DUPLICADOS (409)
public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
