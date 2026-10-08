package com.michaeldev.backend_eykcorp.dto;

import org.springframework.data.domain.Page;

import java.util.List;

// ESTRUCTURA ESTABLE PARA RESPUESTAS PAGINADAS (NO EXPONE LA CLASE Page DE SPRING)
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {

    // CONVIERTE UN Page DE SPRING DATA A LA ESTRUCTURA DE LA API
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
