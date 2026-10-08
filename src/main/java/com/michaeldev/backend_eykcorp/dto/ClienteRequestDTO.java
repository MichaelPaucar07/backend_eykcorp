package com.michaeldev.backend_eykcorp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTO DE SOLICITUD PARA LA ENTIDAD CLIENTE
public record ClienteRequestDTO(
        // VALIDACIONES DE LOS CAMPOS DEL DTO
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
        String apellidos,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe contener solo dígitos (entre 7 y 15)")
        String telefono
) {
}
