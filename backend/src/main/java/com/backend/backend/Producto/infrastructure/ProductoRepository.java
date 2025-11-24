package com.backend.backend.Producto.infrastructure;

import com.backend.backend.Producto.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockGreaterThan(Integer stock);
    List<Producto> findByComercianteId(Long comercianteId);
}
