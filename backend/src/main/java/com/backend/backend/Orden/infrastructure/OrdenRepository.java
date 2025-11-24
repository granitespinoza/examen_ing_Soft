package com.backend.backend.Orden.infrastructure;

import com.backend.backend.Orden.domain.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByClienteIdOrderByFechaOrdenDesc(Long clienteId);
}
