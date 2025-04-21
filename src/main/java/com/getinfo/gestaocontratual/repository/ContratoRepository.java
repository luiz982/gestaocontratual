package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ContratoRepository  extends JpaRepository<Contrato, Long> {
    Optional<Contrato> findById (Long idContrato);
    List<Contrato> findByDtFimBetween(LocalDate start, LocalDate end);
    List<Contrato> findByStatus_NomeIgnoreCase(String nome);
    @Query("SELECT c FROM Contrato c WHERE LOWER(c.status.nome) <> 'arquivado'")
    List<Contrato> findAllAtivos();
}
