package com.michaeldev.backend_eykcorp.repository;

import com.michaeldev.backend_eykcorp.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, Long id);
}