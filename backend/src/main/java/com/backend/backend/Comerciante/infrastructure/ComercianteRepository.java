package com.backend.backend.Comerciante.infrastructure;


import com.backend.backend.Comerciante.domain.Comerciante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComercianteRepository extends JpaRepository<Comerciante, Long> {

    boolean existsByCif(String cif);
    Optional<Comerciante> findByUsuarioId(Long usuarioId);
}
