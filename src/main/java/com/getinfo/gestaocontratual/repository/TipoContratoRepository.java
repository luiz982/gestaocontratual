package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Contratante;
import com.getinfo.gestaocontratual.entities.TipoContrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoContratoRepository extends JpaRepository<TipoContrato, Long> {
    Optional<TipoContrato> findById (Long idTipoContrato);
}