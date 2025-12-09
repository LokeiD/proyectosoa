package com.coopac.sistemasoa.socio.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Socio")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Socio")
    private Integer codSocio;

    @Column(name = "Nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "Apellido_Paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "Apellido_Materno", length = 100)
    private String apellidoMaterno;

    @Column(name = "Dni", nullable = false, unique = true, length = 20)
    private String dni;

    @Column(name = "Fecha_Nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "Estado_Civil", nullable = false, length = 20)
    private String estadoCivil;

    @Column(name = "Direccion", nullable = false, length = 200)
    private String direccion;

    @Column(name = "Departamento", nullable = false, length = 50)
    private String departamento;

    @Column(name = "Provincia", nullable = false, length = 50)
    private String provincia;

    @Column(name = "Distrito", nullable = false, length = 50)
    private String distrito;

    @Column(name = "Sector", nullable = false, length = 50)
    private String sector;

    @Column(name = "Telefono", length = 20)
    private String telefono;

    @Column(name = "E_mail", length = 100)
    private String email;

    @Column(name = "Fecha_Registro")
    private LocalDate fechaRegistro;

    @Column(name = "Estado")
    private Boolean estado;
}