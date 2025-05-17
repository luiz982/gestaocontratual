package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, Long>  {
    List<Colaborador> findByContrato_IdContrato(Long idContrato);

    boolean existsByCpf(String cpf);
}
