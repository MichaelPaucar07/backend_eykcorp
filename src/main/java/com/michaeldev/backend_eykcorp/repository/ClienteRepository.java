package com.michaeldev.backend_eykcorp.repository;

import com.michaeldev.backend_eykcorp.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
// REPOSITORIO DE LA ENTIDAD CLIENTE HEREDA DE JPA REPOSITORY (ACCESO A LA BASE DE DATOS)
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // MÉTODO PARA VERIFICAR SI EXISTE UN CLIENTE POR SU CORREO
    boolean existsByCorreo(String correo);
    // MÉTODO PARA VERIFICAR SI EXISTE UN CLIENTE POR SU CORREO EXCLUYENDO UN ID ESPECÍFICO
    boolean existsByCorreoAndIdNot(String correo, Long id);
}