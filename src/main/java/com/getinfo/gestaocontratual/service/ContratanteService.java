package com.getinfo.gestaocontratual.service;

import com.getinfo.gestaocontratual.controller.dto.ContratanteResumoDTO;
import com.getinfo.gestaocontratual.entities.Contratante;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.repository.ContratanteRepository;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContratanteService {

    private final ContratanteRepository contratanteRepository;
    private final ContratoRepository contratoRepository;

    @Autowired
    public ContratanteService(ContratanteRepository contratanteRepository,
                              ContratoRepository contratoRepository) {
        this.contratanteRepository = contratanteRepository;
        this.contratoRepository = contratoRepository;
    }

    public List<Contratante> listarTodos() {
        return contratanteRepository.findAll();
    }

    public Optional<Contratante> buscarPorId(Long id) {
        return contratanteRepository.findById(id);
    }

    public Optional<Contratante> buscarPorCnpj(String cnpj) {
        return contratanteRepository.findByCnpj(cnpj);
    }

    public Contratante salvar(Contratante contratante) {
        return contratanteRepository.save(contratante);
    }

    public void deletar(Long id) {
        contratanteRepository.deleteById(id);
    }

    public List<ContratanteResumoDTO> listarResumoComContratosAtivos() {
        List<Contratante> contratantes = contratanteRepository.findAll();

        return contratantes.stream()
                .map(contratante -> {
                    long contratosAtivos = contratoRepository.countAtivosByContratante(contratante);

                    return new ContratanteResumoDTO(
                            contratante.getIdContratante(),
                            contratante.getNomeFantasia(),
                            contratante.getCnpj(),
                            contratosAtivos
                    );
                })
                .toList();
    }
}