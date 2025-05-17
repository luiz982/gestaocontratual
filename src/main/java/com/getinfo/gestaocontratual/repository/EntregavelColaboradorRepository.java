package com.getinfo.gestaocontratual.repository;
import com.getinfo.gestaocontratual.entities.EntregaveisColaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntregavelColaboradorRepository extends JpaRepository<EntregaveisColaborador, Integer> {
    List<EntregaveisColaborador> findByEntregavel_IdEntregavel(Integer entregavelId);    
}
