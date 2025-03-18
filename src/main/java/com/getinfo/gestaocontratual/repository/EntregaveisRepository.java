package com.getinfo.gestaocontratual.repository;
import com.getinfo.gestaocontratual.entities.Entregaveis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntregaveisRepository extends JpaRepository<Entregaveis, Long> {
    Optional<Entregaveis> findById (Long idEntregavel);
}
