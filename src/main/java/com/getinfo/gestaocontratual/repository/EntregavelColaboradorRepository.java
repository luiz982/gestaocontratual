package com.getinfo.gestaocontratual.repository;
import com.getinfo.gestaocontratual.entities.EntregaveisColaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregavelColaboradorRepository extends JpaRepository<EntregaveisColaborador, Integer> {
    List<EntregaveisColaborador> findByEntregavel_IdEntregavel(Integer entregavelId);    
}
