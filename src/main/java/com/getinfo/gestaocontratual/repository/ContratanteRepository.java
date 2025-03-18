package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Contratante;
import com.getinfo.gestaocontratual.entities.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContratanteRepository extends JpaRepository<Contratante, Long> {
    Optional<Contratante> findById (Long idContratante);
}
