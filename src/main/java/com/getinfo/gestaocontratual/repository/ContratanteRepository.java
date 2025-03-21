package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Contratante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContratanteRepository extends JpaRepository<Contratante, Long> {
    Optional<Contratante> findByCnpj(String cnpj);
}
