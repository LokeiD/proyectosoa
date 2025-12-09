package com.coopac.sistemasoa.empleado.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Trabajador")
public class Trabajador {

    @Id
    @Column(name = "Cod_Trabajador")
    private Integer codTrabajador;

    @Column(name = "Nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "Apellido_Paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "Apellido_Materno", nullable = false, length = 100)
    private String apellidoMaterno;

    @Column(name = "Telefono", length = 20)
    private String telefono;

    @Column(name = "E_mail", length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name = "Cod_Cargo", nullable = false)
    private Cargo cargo;
}