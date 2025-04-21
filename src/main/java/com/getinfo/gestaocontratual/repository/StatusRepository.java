package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface StatusRepository  extends JpaRepository<Status, Long> {
    Optional<Status> findById (Long idStatus);

    Status findByNomeIgnoreCase(String arquivado);
}
