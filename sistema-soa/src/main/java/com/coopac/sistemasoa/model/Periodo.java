package com.coopac.sistemasoa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Periodo")
public class Periodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Periodo")
    private Integer codPeriodo;

    @Column(name = "Nombre_Periodo", nullable = false, length = 50)
    private String nombrePeriodo;
}