package com.michaeldev.backend_eykcorp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// MAPEO DE LA TABLA CLIENTES EN LA BASE DE DATOS
public class Cliente {
    // IDENTIFICADOR ÚNICO DEL CLIENTE
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;
    // IDENTIFICADOR ÚNICO DEL CLIENTE, NO PUEDE HABER DOS CLIENTES CON EL MISMO CORREO
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(nullable = false, length = 15)
    private String telefono;
    // FECHA DE CREACIÓN DEL CLIENTE, NO PUEDE SER MODIFICADA
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
