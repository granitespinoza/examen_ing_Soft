package com.backend.backend.OrdenItem.infrastructure;

import com.backend.backend.OrdenItem.domain.OrdenItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {
}
