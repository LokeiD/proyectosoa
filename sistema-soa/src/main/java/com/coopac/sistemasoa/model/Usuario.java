package com.coopac.sistemasoa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Usuario")
    private Integer codUsuario;

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @ManyToOne
    @JoinColumn(name = "Cod_Rol")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "Cod_Trabajador")
    private Trabajador trabajador;

    @Column(name = "Estado")
    private Boolean estado;
}