package com.coopac.sistemasoa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Estado_Cuota")
public class EstadoCuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Estado")
    private Integer codEstado;

    @Column(name = "Nombre_Estado", nullable = false, length = 50)
    private String nombreEstado;
}