package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ContratoRepository  extends JpaRepository<Contrato, Long> {
    Optional<Contrato> findById (Long idContrato);
}
