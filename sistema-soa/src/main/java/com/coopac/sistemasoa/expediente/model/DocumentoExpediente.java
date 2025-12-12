package com.coopac.sistemasoa.expediente.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Documento_Expediente")
public class DocumentoExpediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Documento")
    private Integer codDocumento;

    @ManyToOne
    @JoinColumn(name = "Cod_Expediente")
    @JsonBackReference
    private ExpedienteCredito expediente;

    @Column(name = "Nombre_Archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "Fecha_Carga", nullable = false)
    private LocalDateTime fechaCarga;

}