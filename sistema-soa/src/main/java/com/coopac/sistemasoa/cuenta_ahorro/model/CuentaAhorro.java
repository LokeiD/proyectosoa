package com.coopac.sistemasoa.cuenta_ahorro.model;

import com.coopac.sistemasoa.socio.model.Socio;
import com.coopac.sistemasoa.empleado.model.Trabajador;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Cuenta_Ahorro")
public class CuentaAhorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Cuenta")
    private Integer codCuenta;

    @ManyToOne
    @JoinColumn(name = "Cod_Socio")
    private Socio socio;

    @Column(name = "Numero_Cuenta", nullable = false, length = 50, unique = true)
    private String numeroCuenta;

    @Column(name = "Fecha_Apertura")
    private LocalDate fechaApertura;

    @Column(name = "Saldo", precision = 18, scale = 2)
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "Cod_Trabajador")
    private Trabajador trabajador;
}