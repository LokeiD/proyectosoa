package com.coopac.sistemasoa.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SolicitudCreditoDTO {
    private String dniSocio;
    private BigDecimal montoSolicitado;
    private Integer codProducto;
    private Integer codRecurrencia;
    private Integer codRiesgo;
    private Integer codPeriodo;
    private String actividad;
    private Integer codTrabajador;
}