package com.coopac.sistemasoa.expediente.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Riesgo")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Riesgo")
    private Integer codRiesgo;

    @Column(name = "Nombre_Riesgo", nullable = false, length = 50)
    private String nombreRiesgo;
}