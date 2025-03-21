package com.getinfo.gestaocontratual.services;

import com.getinfo.gestaocontratual.entities.Contratante;
import com.getinfo.gestaocontratual.repository.ContratanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContratanteService {

    private final ContratanteRepository contratanteRepository;

    @Autowired
    public ContratanteService(ContratanteRepository contratanteRepository) {
        this.contratanteRepository = contratanteRepository;
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
}
