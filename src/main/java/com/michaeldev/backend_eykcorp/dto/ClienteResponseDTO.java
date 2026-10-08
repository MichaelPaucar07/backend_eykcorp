package com.michaeldev.backend_eykcorp.dto;

import java.time.LocalDateTime;

// DTO DE RESPUESTA PARA LA ENTIDAD CLIENTE
public record ClienteResponseDTO(
        Long id,
        String nombres,
        String apellidos,
        String correo,
        String telefono,
        LocalDateTime fechaCreacion
) {
}
