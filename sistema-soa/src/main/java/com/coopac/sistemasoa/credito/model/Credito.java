package com.coopac.sistemasoa.credito.model;

import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.model.Periodo;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Credito")
public class Credito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Credito")
    private Integer codCredito;

    @OneToOne
    @JoinColumn(name = "Cod_Expediente")
    private ExpedienteCredito expediente;

    @Column(name = "Monto_Aprobado", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoAprobado;

    @Column(name = "Tasa_Mensual", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaMensual;

    @Column(name = "Tasa_Anual", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaAnual;

    @ManyToOne
    @JoinColumn(name = "Cod_Periodo")
    private Periodo periodo;

    @Column(name = "Descripcion_Resolucion", length = 255)
    private String descripcionResolucion;

    @Column(name = "Numero_Cuotas", nullable = false)
    private Integer numeroCuotas;

    @Column(name = "Estado")
    private Boolean estado;
}