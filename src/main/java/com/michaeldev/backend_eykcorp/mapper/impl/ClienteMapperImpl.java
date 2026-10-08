package com.michaeldev.backend_eykcorp.mapper.impl;

import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;
import com.michaeldev.backend_eykcorp.entity.Cliente;
import com.michaeldev.backend_eykcorp.mapper.ClienteMapper;
import org.springframework.stereotype.Component;

// IMPLEMENTACIÓN DEL MAPPER DE CLIENTE
@Component
public class ClienteMapperImpl implements ClienteMapper {

    // METODO PARA CONVERTIR UN DTO DE SOLICITUD A UNA ENTIDAD CLIENTE
    @Override
    public Cliente toEntity(ClienteRequestDTO dto) {
        return Cliente.builder()
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .correo(dto.correo())
                .telefono(dto.telefono())
                .build();
    }

    // METODO PARA CONVERTIR UNA ENTIDAD CLIENTE A UN DTO DE RESPUESTA
    @Override
    public ClienteResponseDTO toResponse(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getCorreo(),
                cliente.getTelefono(),
                cliente.getFechaCreacion());
    }

    // METODO PARA ACTUALIZAR UNA ENTIDAD CLIENTE CON LOS DATOS DE UN DTO DE
    // SOLICITUD
    @Override
    public void updateEntity(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNombres(dto.nombres());
        cliente.setApellidos(dto.apellidos());
        cliente.setCorreo(dto.correo());
        cliente.setTelefono(dto.telefono());
    }
}
