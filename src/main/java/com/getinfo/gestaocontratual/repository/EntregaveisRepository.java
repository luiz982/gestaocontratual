package com.getinfo.gestaocontratual.repository;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.Entregaveis;

import com.getinfo.gestaocontratual.entities.StatusEntregavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaveisRepository extends JpaRepository<Entregaveis, Long> {
    Optional<Entregaveis> findById (Long idEntregavel);
    List<Entregaveis> findByIdContrato_IdContrato(Long idContrato);
    void deleteAllByIdContrato_IdContrato(Long idContrato);
    List<Entregaveis> findByDtFimBetween(LocalDate dtInicio, LocalDate dtFinal);
    List<Entregaveis> findByStatus(StatusEntregavel status);

}
