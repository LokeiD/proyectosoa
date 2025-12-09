package com.coopac.sistemasoa.credito.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Cuotas")
public class Cuotas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Cuota")
    private Integer codCuota;

    @ManyToOne
    @JoinColumn(name = "Cod_Credito")
    private Credito credito;

    @Column(name = "Monto_Cuota", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoCuota;

    @Column(name = "Monto_Interes", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoInteres;

    @Column(name = "Fecha_Vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "Fecha_Pago")
    private LocalDate fechaPago;

    @ManyToOne
    @JoinColumn(name = "Cod_Estado")
    private EstadoCuota estadoCuota;
}