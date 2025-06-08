package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Repactuacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepactuacaoRepository extends JpaRepository<Repactuacao, Long> {

    /**
     * Busca todas as repactuações de um contrato específico
     * @param idContrato ID do contrato
     * @return Lista de repactuações do contrato
     */
    List<Repactuacao> findByIdContrato_IdContrato(Long idContrato);

    /**
     * Busca repactuações por nome (útil para buscar repactuações específicas)
     * @param nome Nome da repactuação
     * @return Lista de repactuações com o nome especificado
     */
    List<Repactuacao> findByNomeContainingIgnoreCase(String nome);

    /**
     * Conta quantas repactuações existem para um contrato
     * @param idContrato ID do contrato
     * @return Número de repactuações do contrato
     */
    @Query("SELECT COUNT(r) FROM Repactuacao r WHERE r.idContrato.idContrato = :idContrato")
    Long countByIdContrato(@Param("idContrato") Long idContrato);

    /**
     * Busca a última repactuação de um contrato (mais recente)
     * @param idContrato ID do contrato
     * @return Última repactuação do contrato
     */
    @Query("SELECT r FROM Repactuacao r WHERE r.idContrato.idContrato = :idContrato ORDER BY r.createdAt DESC")
    List<Repactuacao> findLastRepactuacaoByContrato(@Param("idContrato") Long idContrato);
}