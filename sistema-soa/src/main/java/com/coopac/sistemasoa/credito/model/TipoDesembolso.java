package com.coopac.sistemasoa.credito.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Tipo_Desembolso")
public class TipoDesembolso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Tipo_Desembolso")
    private Integer codTipoDesembolso;

    @Column(name = "Nombre", nullable = false, length = 50)
    private String nombre;
}