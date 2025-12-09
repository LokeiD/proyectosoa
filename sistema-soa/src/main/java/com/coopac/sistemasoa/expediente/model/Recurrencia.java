package com.coopac.sistemasoa.expediente.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Recurrencia")
public class Recurrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Recurrencia")
    private Integer codRecurrencia;

    @Column(name = "Nombre_Recurrencia", nullable = false, length = 50)
    private String nombreRecurrencia;
}