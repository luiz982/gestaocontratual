package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Contratante;
import com.getinfo.gestaocontratual.entities.Contrato;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository  extends JpaRepository<Contrato, Long> {
    Optional<Contrato> findById (Long idContrato);
    List<Contrato> findByDtFimBetween(LocalDate start, LocalDate end);
    List<Contrato> findByStatus_NomeIgnoreCase(String nome);
    @Query("SELECT c FROM Contrato c WHERE LOWER(c.status.nome) <> 'arquivado'")
    List<Contrato> findAllAtivos();
    @Query("SELECT MONTH(c.dtInicio), COUNT(c) FROM Contrato c WHERE YEAR(c.dtInicio) = :ano GROUP BY MONTH(c.dtInicio)")
    List<Object[]> countContratosPorMes(@Param("ano") int ano);

    List<Contrato> findAll();

    long countByStatus_NomeIgnoreCase(String nome);

    @Query("SELECT COUNT(c) FROM Contrato c WHERE LOWER(c.status.nome) <> 'arquivado'")
    long countContratosAtivos();

    @Query("SELECT COUNT(c) FROM Contrato c WHERE c.idContratante = :contratante AND LOWER(c.status.nome) <> 'arquivado'")
    long countAtivosByContratante(@Param("contratante") Contratante contratante);

    @Query("SELECT c.idContratante.regiao, COUNT(c) FROM Contrato c GROUP BY c.idContratante.regiao")
    List<Object[]> countContratosPorRegiao();
}
