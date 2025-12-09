package com.coopac.sistemasoa.socio.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Aportes")
public class Aportes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Aporte")
    private Integer codAporte;

    @ManyToOne
    @JoinColumn(name = "Cod_Socio")
    private Socio socio;

    @Column(name = "Monto", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "Fecha_Aporte")
    private LocalDate fechaAporte;

    @Column(name = "Descripcion", length = 255)
    private String descripcion;

    @Column(name = "Estado")
    private Boolean estado;
}