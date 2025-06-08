package com.getinfo.gestaocontratual.service;

import com.getinfo.gestaocontratual.controller.dto.RepactuacaoRequestDTO;
import com.getinfo.gestaocontratual.controller.dto.RepactuacaoResponseDTO;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.Repactuacao;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import com.getinfo.gestaocontratual.repository.RepactuacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepactuacaoService {

    @Autowired
    private RepactuacaoRepository repactuacaoRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Transactional
    public RepactuacaoResponseDTO criarRepactuacao(RepactuacaoRequestDTO requestDTO) {
        Contrato contrato = contratoRepository.findById(requestDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado com ID: " + requestDTO.getIdContrato()));

        // Criar a repactuação
        Repactuacao repactuacao = new Repactuacao(
                contrato,
                requestDTO.getDtFimContrato(),
                requestDTO.getNome(),
                requestDTO.getDescricao()
        );

        // CORREÇÃO: Atualizar o contrato com a nova data fim
        contrato.setDtFim(requestDTO.getDtFimContrato());
        contrato.setDtAlteracao(new Date()); // Registrar quando foi alterado
        contratoRepository.save(contrato);

        // Salvar a repactuação
        Repactuacao repactuacaoSalva = repactuacaoRepository.save(repactuacao);

        return converterParaDTO(repactuacaoSalva);
    }

    public List<RepactuacaoResponseDTO> listarTodasRepactuacoes() {
        List<Repactuacao> repactuacoes = repactuacaoRepository.findAll();
        return repactuacoes.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public RepactuacaoResponseDTO buscarRepactuacaoPorId(Long id) {
        Repactuacao repactuacao = repactuacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repactuação não encontrada com ID: " + id));
        return converterParaDTO(repactuacao);
    }

    public List<RepactuacaoResponseDTO> listarRepactuacoesPorContrato(Long idContrato) {
        List<Repactuacao> repactuacoes = repactuacaoRepository.findByIdContrato_IdContrato(idContrato);
        return repactuacoes.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RepactuacaoResponseDTO atualizarRepactuacao(Long id, RepactuacaoRequestDTO requestDTO) {
        Repactuacao repactuacao = repactuacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repactuação não encontrada com ID: " + id));

        Contrato contrato = contratoRepository.findById(requestDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado com ID: " + requestDTO.getIdContrato()));

        repactuacao.setIdContrato(contrato);
        repactuacao.setDtFimContrato(requestDTO.getDtFimContrato());
        repactuacao.setNome(requestDTO.getNome());
        repactuacao.setDescricao(requestDTO.getDescricao());

        // CORREÇÃO: Também atualizar o contrato quando editar a repactuação
        contrato.setDtFim(requestDTO.getDtFimContrato());
        contrato.setDtAlteracao(new Date());
        contratoRepository.save(contrato);

        Repactuacao repactuacaoAtualizada = repactuacaoRepository.save(repactuacao);
        return converterParaDTO(repactuacaoAtualizada);
    }

    @Transactional
    public void deletarRepactuacao(Long id) {
        if (!repactuacaoRepository.existsById(id)) {
            throw new RuntimeException("Repactuação não encontrada com ID: " + id);
        }
        repactuacaoRepository.deleteById(id);
    }

    @Transactional
    public void aplicarRepactuacao(Long idRepactuacao) {
        Repactuacao repactuacao = repactuacaoRepository.findById(idRepactuacao)
                .orElseThrow(() -> new RuntimeException("Repactuação não encontrada com ID: " + idRepactuacao));

        Contrato contrato = repactuacao.getIdContrato();

        // Atualiza a data fim do contrato com a nova data da repactuação
        contrato.setDtFim(repactuacao.getDtFimContrato());
        contrato.setDtAlteracao(new Date());

        // Salva o contrato com a nova data
        contratoRepository.save(contrato);
    }

    private RepactuacaoResponseDTO converterParaDTO(Repactuacao repactuacao) {
        RepactuacaoResponseDTO dto = new RepactuacaoResponseDTO();
        dto.setIdRepactuacao(repactuacao.getIdRepactuacao());
        dto.setIdContrato(repactuacao.getIdContrato().getIdContrato());
        dto.setNumeroContrato(repactuacao.getIdContrato().getNumContrato());
        dto.setDtFimContrato(repactuacao.getDtFimContrato());
        dto.setNome(repactuacao.getNome());
        dto.setDescricao(repactuacao.getDescricao());
        dto.setCreatedAt(repactuacao.getCreatedAt());
        return dto;
    }
}