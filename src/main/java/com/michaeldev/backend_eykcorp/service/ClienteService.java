package com.michaeldev.backend_eykcorp.service;

import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;

import java.util.List;

// INTERFAZ DEL SERVICIO PARA LA ENTIDAD CLIENTE
public interface ClienteService {

    ClienteResponseDTO crear(ClienteRequestDTO request);

    List<ClienteResponseDTO> listar();

    ClienteResponseDTO obtenerPorId(Long id);

    ClienteResponseDTO actualizar(Long id, ClienteRequestDTO request);

    void eliminar(Long id);
}