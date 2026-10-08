package com.michaeldev.backend_eykcorp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTO DE SOLICITUD PARA LA ENTIDAD CLIENTE
public record ClienteRequestDTO(
        // VALIDACIONES DE LOS CAMPOS DEL DTO
        // SOLO LETRAS (CON TILDES Y Ñ), SEPARADAS POR UN ESPACIO, APÓSTROFO O GUION
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
        @Pattern(regexp = NOMBRE_REGEX, message = "Los nombres solo pueden contener letras")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
        @Pattern(regexp = NOMBRE_REGEX, message = "Los apellidos solo pueden contener letras")
        String apellidos,

        // FORMATO usuario@dominio.ext (EXIGE EXTENSIÓN DE DOMINIO, EJ. .com)
        @NotBlank(message = "El correo es obligatorio")
        @Email(regexp = CORREO_REGEX, message = "El correo no tiene un formato válido")
        @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
        String correo,

        // FORMATO INTERNACIONAL E.164: +<CÓDIGO DE PAÍS><NÚMERO>, EJ. +593991234567
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = TELEFONO_REGEX,
                message = "El teléfono debe tener formato internacional con código de país, ej. +593991234567")
        String telefono) {

    public static final String NOMBRE_REGEX = "^\\p{L}+(?:[ '\\-]\\p{L}+)*$";
    public static final String CORREO_REGEX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$";
    public static final String TELEFONO_REGEX = "^\\+[1-9][0-9]{7,13}$";

    // NORMALIZACIÓN DE LOS DATOS DE ENTRADA (SE EJECUTA ANTES DE LAS VALIDACIONES)
    public ClienteRequestDTO {
        nombres = limpiarTexto(nombres);
        apellidos = limpiarTexto(apellidos);
        correo = correo == null ? null : correo.trim().toLowerCase();
        telefono = telefono == null ? null : telefono.replaceAll("[\\s\\-()]", "");
    }

    // QUITA ESPACIOS AL INICIO/FIN Y DEJA UN SOLO ESPACIO ENTRE PALABRAS
    private static String limpiarTexto(String valor) {
        return valor == null ? null : valor.trim().replaceAll("\\s+", " ");
    }
}
