package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ContratoRepository  extends JpaRepository<Contrato, Long> {
    Optional<Contrato> findById (Long idContrato);
    List<Contrato> findByDtFimBetween(LocalDate start, LocalDate end);
    List<Contrato> findByStatus_NomeIgnoreCase(String nome);
    @Query("SELECT c FROM Contrato c WHERE LOWER(c.status.nome) <> 'arquivado'")
    List<Contrato> findAllAtivos();
    @Query("SELECT MONTH(c.createdAt), COUNT(c) FROM Contrato c WHERE YEAR(c.createdAt) = :ano GROUP BY MONTH(c.createdAt)")
    List<Object[]> countContratosPorMes(@Param("ano") int ano);

    long countByTipoContratoIgnoreCase(String publico);
}
