package com.michaeldev.backend_eykcorp.service.impl;

import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;
import com.michaeldev.backend_eykcorp.entity.Cliente;
import com.michaeldev.backend_eykcorp.exception.DuplicateResourceException;
import com.michaeldev.backend_eykcorp.exception.ResourceNotFoundException;
import com.michaeldev.backend_eykcorp.mapper.ClienteMapper;
import com.michaeldev.backend_eykcorp.repository.ClienteRepository;
import com.michaeldev.backend_eykcorp.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
// IMPLEMENTACIÓN DEL SERVICIO PARA LA ENTIDAD CLIENTE
public class ClienteServiceImpl implements ClienteService {
    // REPOSITORIO DE CLIENTE PARA ACCEDER A LA BASE DE DATOS
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    // MÉTODO PARA CREAR UN NUEVO CLIENTE
    @Override
    @Transactional
    public ClienteResponseDTO crear(ClienteRequestDTO request) {
        String correo = clienteMapper.normalizarCorreo(request.correo());
        if (clienteRepository.existsByCorreo(correo)) {
            throw new DuplicateResourceException("Ya existe un cliente registrado con el correo: " + correo);
        }
        // Guardar el cliente en la base de datos
        Cliente guardado = clienteRepository.save(clienteMapper.toEntity(request));
        log.info("Cliente creado con id {}", guardado.getId());
        return clienteMapper.toResponse(guardado);
    }

    // MÉTODO PARA LISTAR TODOS LOS CLIENTES
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listar() {
        log.debug("Listando clientes");
        // Obtener todos los clientes de la base de datos y convertirlos a DTOs de
        // respuesta
        return clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    // MÉTODO PARA OBTENER UN CLIENTE POR SU ID
    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando cliente con id {}", id);
        // Buscar el cliente en la base de datos o lanzar una excepción si no existe
        return clienteMapper.toResponse(buscarCliente(id));
    }

    // MÉTODO PARA ACTUALIZAR UN CLIENTE EXISTENTE
    @Override
    @Transactional
    public ClienteResponseDTO actualizar(Long id, ClienteRequestDTO request) {
        Cliente cliente = buscarCliente(id);

        String correo = clienteMapper.normalizarCorreo(request.correo());
        // Verificar si el correo ya pertenece a otro cliente
        if (clienteRepository.existsByCorreoAndIdNot(correo, id)) {
            throw new DuplicateResourceException("El correo " + correo + " ya pertenece a otro cliente");
        }

        clienteMapper.updateEntity(cliente, request);
        Cliente actualizado = clienteRepository.save(cliente);
        log.info("Cliente con id {} actualizado", id);
        return clienteMapper.toResponse(actualizado);
    }

    // MÉTODO PARA ELIMINAR UN CLIENTE EXISTENTE
    @Override
    @Transactional
    public void eliminar(Long id) {
        // Verificar si el cliente existe antes de eliminarlo
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontro el cliente con id: " + id);
        }
        clienteRepository.deleteById(id);
        log.info("Cliente con id {} eliminado", id);
    }

    // MÉTODO PRIVADO PARA BUSCAR UN CLIENTE POR SU ID O LANZAR UNA EXCEPCIÓN SI NO
    // EXISTE
    private Cliente buscarCliente(Long id) {
        // Buscar el cliente en la base de datos o lanzar una excepción si no existe
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el cliente con id: " + id));
    }
}