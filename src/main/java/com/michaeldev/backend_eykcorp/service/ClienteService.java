package com.michaeldev.backend_eykcorp.service;

import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;
import com.michaeldev.backend_eykcorp.dto.PageResponse;

// INTERFAZ DEL SERVICIO PARA LA ENTIDAD CLIENTE
public interface ClienteService {

    ClienteResponseDTO crear(ClienteRequestDTO request);

    PageResponse<ClienteResponseDTO> listar(int page, int size);

    ClienteResponseDTO obtenerPorId(Long id);

    ClienteResponseDTO actualizar(Long id, ClienteRequestDTO request);

    void eliminar(Long id);
}