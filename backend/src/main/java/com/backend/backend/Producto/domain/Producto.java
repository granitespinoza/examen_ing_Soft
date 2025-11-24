package com.backend.backend.Producto.domain;


import com.backend.backend.Comerciante.domain.Comerciante;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comerciante_id", nullable = false)
    private Comerciante comerciante;
    private String nombre;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private CategoriaProducto categoria;


    private BigDecimal precio;
    private Integer stock;

    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate(){
        fechaCreacion = LocalDateTime.now();
    }

}
