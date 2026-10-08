package com.michaeldev.backend_eykcorp.exception;

import org.springframework.http.HttpStatus;

// EXCEPCIÓN BASE DE NEGOCIO: CADA SUBCLASE DEFINE SU PROPIO CÓDIGO HTTP
public abstract class BusinessException extends RuntimeException {

    private final HttpStatus status;

    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
