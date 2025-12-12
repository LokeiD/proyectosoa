package com.coopac.sistemasoa.expediente.model;

import com.coopac.sistemasoa.empleado.model.Trabajador;
import com.coopac.sistemasoa.socio.model.Socio;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "Expediente_Credito")
public class ExpedienteCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Expediente")
    private Integer codExpediente;

    @ManyToOne
    @JoinColumn(name = "Cod_Socio")
    private Socio socio;

    @ManyToOne
    @JoinColumn(name = "Cod_Producto")
    private ProductoCrediticio producto;

    @ManyToOne
    @JoinColumn(name = "Cod_Recurrencia")
    private Recurrencia recurrencia;

    @ManyToOne
    @JoinColumn(name = "Cod_Riesgo")
    private Riesgo riesgo;

    @ManyToOne
    @JoinColumn(name = "Cod_Periodo")
    private Periodo periodo;

    @ManyToOne
    @JoinColumn(name = "Cod_Trabajador")
    private Trabajador trabajador;

    @Column(name = "Monto_Solicitado", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoSolicitado;

    @Column(name = "Actividad", nullable = false, length = 255)
    private String actividad;

    @Column(name = "Fecha_Solicitud")
    private LocalDate fechaSolicitud;

    @Column(name = "Estado")
    private Boolean estado;

    @OneToMany(mappedBy = "expediente", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<DocumentoExpediente> documentos;
}