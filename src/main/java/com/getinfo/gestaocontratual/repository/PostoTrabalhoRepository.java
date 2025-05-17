package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.PostoTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostoTrabalhoRepository extends JpaRepository<PostoTrabalho, Long> {
    List<PostoTrabalho> findByIdContrato_IdContrato(Long idContrato);

    void deleteAllByIdContrato_IdContrato(Long idContrato);
}
