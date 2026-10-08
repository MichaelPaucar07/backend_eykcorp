package com.michaeldev.backend_eykcorp.service.impl;

import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;
import com.michaeldev.backend_eykcorp.dto.PageResponse;
import com.michaeldev.backend_eykcorp.entity.Cliente;
import com.michaeldev.backend_eykcorp.exception.DuplicateResourceException;
import com.michaeldev.backend_eykcorp.exception.ResourceNotFoundException;
import com.michaeldev.backend_eykcorp.mapper.ClienteMapper;
import com.michaeldev.backend_eykcorp.repository.ClienteRepository;
import com.michaeldev.backend_eykcorp.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (clienteRepository.existsByCorreo(request.correo())) {
            throw new DuplicateResourceException("Ya existe un cliente registrado con el correo: " + request.correo());
        }
        // Guardar el cliente en la base de datos
        Cliente guardado = clienteRepository.save(clienteMapper.toEntity(request));
        log.info("Cliente creado con id {}", guardado.getId());
        return clienteMapper.toResponse(guardado);
    }

    // MÉTODO PARA LISTAR LOS CLIENTES DE FORMA PAGINADA
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClienteResponseDTO> listar(int page, int size) {
        log.debug("Listando clientes: página {}, tamaño {}", page, size);
        // Obtener la página solicitada ordenada por id y convertirla a DTOs de
        // respuesta
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<ClienteResponseDTO> clientes = clienteRepository.findAll(pageable)
                .map(clienteMapper::toResponse);
        return PageResponse.from(clientes);
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

        // Verificar si el correo ya pertenece a otro cliente
        if (clienteRepository.existsByCorreoAndIdNot(request.correo(), id)) {
            throw new DuplicateResourceException("El correo " + request.correo() + " ya pertenece a otro cliente");
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