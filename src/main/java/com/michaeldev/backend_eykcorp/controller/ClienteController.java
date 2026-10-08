package com.michaeldev.backend_eykcorp.controller;

import com.michaeldev.backend_eykcorp.dto.ApiResponse;
import com.michaeldev.backend_eykcorp.dto.ClienteRequestDTO;
import com.michaeldev.backend_eykcorp.dto.ClienteResponseDTO;
import com.michaeldev.backend_eykcorp.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// CONTROLADOR REST PARA LA GESTIÓN DE CLIENTES
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // POST /clientes -> 201 CREATED + HEADER LOCATION
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> crear(@Valid @RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO creado = clienteService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location)
                .body(ApiResponse.success(HttpStatus.CREATED, "Cliente creado correctamente", creado));
    }

    // GET /clientes -> 200 OK
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponseDTO>>> listar() {
        List<ClienteResponseDTO> clientes = clienteService.listar();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Clientes obtenidos correctamente", clientes));
    }

    // GET /clientes/{id} -> 200 OK | 404 NOT FOUND
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Cliente obtenido correctamente", cliente));
    }

    // PUT /clientes/{id} -> 200 OK | 404 NOT FOUND | 409 CONFLICT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> actualizar(@PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO actualizado = clienteService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Cliente actualizado correctamente", actualizado));
    }

    // DELETE /clientes/{id} -> 200 OK | 404 NOT FOUND
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Cliente eliminado correctamente", null));
    }
}
