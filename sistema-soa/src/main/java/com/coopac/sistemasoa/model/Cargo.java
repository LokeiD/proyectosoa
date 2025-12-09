package com.coopac.sistemasoa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Cargo")
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Cargo")
    private Integer codCargo;

    @Column(name = "Nombre", nullable = false, length = 100)
    private String nombre;
}