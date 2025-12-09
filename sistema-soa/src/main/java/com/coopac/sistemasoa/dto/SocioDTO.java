package com.coopac.sistemasoa.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SocioDTO {
    private Integer codSocio;
    private String dni;
    private String nombresCompletos;
    private Integer edad;
    private LocalDate fechaNacimiento;
    private String estadoCivil;
    private String domicilio;
    private String departamento;
    private String provincia;
    private String distrito;
    private String sector;
}