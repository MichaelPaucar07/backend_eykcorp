package com.michaeldev.backend_eykcorp.mapper;

import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;
import com.michaeldev.backend_eykcorp.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto) {
        return Cliente.builder()
                .nombres(dto.nombres().trim())
                .apellidos(dto.apellidos().trim())
                .correo(normalizarCorreo(dto.correo()))
                .telefono(dto.telefono().trim())
                .build();
    }

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

    public void updateEntity(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNombres(dto.nombres().trim());
        cliente.setApellidos(dto.apellidos().trim());
        cliente.setCorreo(normalizarCorreo(dto.correo()));
        cliente.setTelefono(dto.telefono().trim());
    }

    public String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }
}
