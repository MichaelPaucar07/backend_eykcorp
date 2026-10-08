package com.michaeldev.backend_eykcorp.mapper;

import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;
import com.michaeldev.backend_eykcorp.entity.Cliente;

// CONTRATO DE CONVERSIÓN ENTRE LA ENTIDAD CLIENTE Y SUS DTOs
public interface ClienteMapper {

    // CONVIERTE UN DTO DE SOLICITUD A UNA ENTIDAD CLIENTE
    Cliente toEntity(ClienteRequestDTO dto);

    // CONVIERTE UNA ENTIDAD CLIENTE A UN DTO DE RESPUESTA
    ClienteResponseDTO toResponse(Cliente cliente);

    // ACTUALIZA UNA ENTIDAD CLIENTE CON LOS DATOS DE UN DTO DE SOLICITUD
    void updateEntity(Cliente cliente, ClienteRequestDTO dto);
}
