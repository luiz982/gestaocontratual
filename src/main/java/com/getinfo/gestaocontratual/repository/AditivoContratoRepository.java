package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.AditivoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AditivoContratoRepository extends JpaRepository<AditivoContrato, Long> {
    List<AditivoContrato> findByContrato_IdContrato(Long idContrato);
}
