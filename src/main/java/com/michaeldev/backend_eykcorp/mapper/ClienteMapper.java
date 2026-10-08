package com.michaeldev.backend_eykcorp.mapper;

import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;
import com.michaeldev.backend_eykcorp.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    // METODO PARA CONVERTIR UN DTO DE SOLICITUD A UNA ENTIDAD CLIENTE
    public Cliente toEntity(ClienteRequestDTO dto) {
        return Cliente.builder()
                .nombres(dto.nombres().trim())
                .apellidos(dto.apellidos().trim())
                .correo(normalizarCorreo(dto.correo()))
                .telefono(dto.telefono().trim())
                .build();
    }
    // METODO PARA CONVERTIR UNA ENTIDAD CLIENTE A UN DTO DE RESPUESTA
    public ClienteResponseDTO toResponse(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getCorreo(),
                cliente.getTelefono(),
                cliente.getFechaCreacion()
        );
    }
    // METODO PARA ACTUALIZAR UNA ENTIDAD CLIENTE CON LOS DATOS DE UN DTO DE SOLICITUD
    public void updateEntity(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNombres(dto.nombres().trim());
        cliente.setApellidos(dto.apellidos().trim());
        cliente.setCorreo(normalizarCorreo(dto.correo()));
        cliente.setTelefono(dto.telefono().trim());
    }
    // METODO PARA NORMALIZAR EL CORREO ELECTRÓNICO
    public String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }
}