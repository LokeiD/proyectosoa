package com.coopac.sistemasoa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Movimiento")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Movimiento")
    private Integer codMovimiento;

    @ManyToOne
    @JoinColumn(name = "Cod_Cuenta")
    private CuentaAhorro cuentaAhorro;

    @Column(name = "Tipo_Movimiento")
    private Boolean tipoMovimiento;

    @Column(name = "Monto", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "Fecha")
    private LocalDateTime fecha;
}