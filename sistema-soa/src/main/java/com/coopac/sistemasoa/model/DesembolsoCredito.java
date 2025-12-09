package com.coopac.sistemasoa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Desembolso_Credito")
public class DesembolsoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Desembolso")
    private Integer codDesembolso;

    @ManyToOne
    @JoinColumn(name = "Cod_Credito")
    private Credito credito;

    @Column(name = "Serie", nullable = false, length = 20)
    private String serie;

    @Column(name = "Fecha", nullable = false)
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "Cod_Tipo_Desembolso")
    private TipoDesembolso tipoDesembolso;

    @Column(name = "Cod_Contrato", nullable = false, length = 50)
    private String codContrato;

    @Column(name = "Cod_Titulo_Valor", nullable = false, length = 50)
    private String codTituloValor;

    @ManyToOne
    @JoinColumn(name = "Cod_Trabajador")
    private Trabajador trabajador;
}