package com.getinfo.gestaocontratual.repository;
import com.getinfo.gestaocontratual.entities.EntregaveisColaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaveisColaboradorRepository extends JpaRepository<EntregaveisColaborador, Long> {
    List<EntregaveisColaborador> findByEntregavel_IdEntregavel(Long entregavelId);
}
