package com.backend.backend.Orden.domain;


import com.backend.backend.OrdenItem.domain.OrdenItem;
import com.backend.backend.users.domain.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;


    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL , orphanRemoval = true)
    @Builder.Default
    private List<OrdenItem> items = new ArrayList<>();


    private LocalDateTime fechaOrden;

    private EstadoOrden estado;

    private BigDecimal total;

    private  String direccionEnvio;


    @PrePersist
    protected void onCreate(){
        fechaOrden = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoOrden.PENDIENTE;
        }
    }

}
