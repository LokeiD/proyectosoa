package com.coopac.sistemasoa.expediente.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Producto_Crediticio")
public class ProductoCrediticio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod_Producto")
    private Integer codProducto;

    @Column(name = "Nombre_Producto", nullable = false, length = 100)
    private String nombreProducto;
}