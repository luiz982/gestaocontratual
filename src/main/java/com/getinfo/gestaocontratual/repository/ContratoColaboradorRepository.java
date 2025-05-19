package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.ContratoColaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoColaboradorRepository extends JpaRepository<ContratoColaborador, Long> {
    List<ContratoColaborador> findByContrato_IdContrato(Long contratoId);
}
