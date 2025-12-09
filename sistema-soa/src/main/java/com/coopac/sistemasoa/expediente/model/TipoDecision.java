package com.coopac.sistemasoa.expediente.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Tipo_Decision")
public class TipoDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Tipo_Decision")
    private Integer codTipoDecision;

    @Column(name = "Nombre", nullable = false, length = 50)
    private String nombre;
}