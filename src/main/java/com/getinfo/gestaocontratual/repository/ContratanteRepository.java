package com.getinfo.gestaocontratual.repository;
import com.getinfo.gestaocontratual.entities.Contratante;
import com.getinfo.gestaocontratual.entities.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContratanteRepository extends JpaRepository<Contratante, Long> {
    Optional<Contratante> findById(Long idContratante);

    Optional<Contratante> findByCnpj(String cnpj);

    List<Contratante> findByTipoEmpresa(Integer tipoEmpresa);

    long countByTipoEmpresa(Integer tipoEmpresa);

    List<Contratante> findByTipoEmpresaAndEstado(Integer tipoEmpresa, String estado);

    List<Contratante> findByTipoEmpresaAndRegiao(Integer tipoEmpresa, String regiao);
}
