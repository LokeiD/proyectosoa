package com.coopac.sistemasoa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Rol")
    private Integer codRol;

    @Column(name = "Nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "Estado")
    private Boolean estado;
}