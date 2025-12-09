package com.coopac.sistemasoa.expediente.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Evaluacion")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Evaluacion")
    private Integer codEvaluacion;

    @ManyToOne
    @JoinColumn(name = "Cod_Tipo_Decision")
    private TipoDecision tipoDecision;

    @Column(name = "Fecha_Envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "Descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "Cod_Expediente")
    private ExpedienteCredito expediente;
}