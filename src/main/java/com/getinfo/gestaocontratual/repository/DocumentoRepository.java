package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Documentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documentos, Long> {
    @Query("SELECT d FROM Documentos d WHERE d.idContrato.idContrato = :idContrato")
    List<Documentos> findByContratoId(@Param("idContrato") Long idContrato);
}