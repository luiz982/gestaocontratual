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

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        // DEBUG: Verificar a data recebida
        System.out.println("=== DEBUG CRIAÇÃO REPACTUAÇÃO ===");
        System.out.println("Data recebida no DTO: " + requestDTO.getDtFimContrato());
        if (requestDTO.getDtFimContrato() != null) {
            System.out.println("Data formatada: " + new SimpleDateFormat("yyyy-MM-dd").format(requestDTO.getDtFimContrato()));
            System.out.println("Timezone da data: " + requestDTO.getDtFimContrato().getTimezoneOffset());
        }

        // CORREÇÃO: Ajustar timezone da data
        Date dataCorrigida = ajustarTimezone(requestDTO.getDtFimContrato());

        Contrato contrato = contratoRepository.findById(requestDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado com ID: " + requestDTO.getIdContrato()));

        System.out.println("Data original do contrato: " + contrato.getDtFim());

        // Criar a repactuação com data corrigida
        Repactuacao repactuacao = new Repactuacao(
                contrato,
                dataCorrigida,
                requestDTO.getNome(),
                requestDTO.getDescricao()
        );

        System.out.println("Data após correção de timezone: " + dataCorrigida);

        // Salvar a repactuação PRIMEIRO
        Repactuacao repactuacaoSalva = repactuacaoRepository.save(repactuacao);

        // Aplicar automaticamente a repactuação ao contrato
        aplicarRepactuacaoInterna(repactuacaoSalva.getIdRepactuacao());

        System.out.println("================================");

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

    private Date ajustarTimezone(Date data) {
        if (data == null) return null;

        // Opção 1: Forçar horário meio-dia para evitar problemas de timezone
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public List<RepactuacaoResponseDTO> listarRepactuacoesPorContrato(Long idContrato) {
        List<Repactuacao> repactuacoes = repactuacaoRepository.findByIdContrato_IdContrato(idContrato);
        return repactuacoes.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RepactuacaoResponseDTO atualizarRepactuacao(Long id, RepactuacaoRequestDTO requestDTO) {
        System.out.println("=== DEBUG ATUALIZAÇÃO REPACTUAÇÃO ===");
        System.out.println("Data recebida no DTO: " + requestDTO.getDtFimContrato());

        // CORREÇÃO: Ajustar timezone da data
        Date dataCorrigida = ajustarTimezone(requestDTO.getDtFimContrato());

        Repactuacao repactuacao = repactuacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repactuação não encontrada com ID: " + id));

        Contrato contrato = contratoRepository.findById(requestDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado com ID: " + requestDTO.getIdContrato()));

        repactuacao.setIdContrato(contrato);
        repactuacao.setDtFimContrato(dataCorrigida);
        repactuacao.setNome(requestDTO.getNome());
        repactuacao.setDescricao(requestDTO.getDescricao());

        System.out.println("Data após correção de timezone: " + dataCorrigida);

        Repactuacao repactuacaoAtualizada = repactuacaoRepository.save(repactuacao);

        // Aplicar automaticamente a atualização ao contrato
        aplicarRepactuacaoInterna(repactuacaoAtualizada.getIdRepactuacao());

        System.out.println("====================================");

        return converterParaDTO(repactuacaoAtualizada);
    }

    @Transactional
    public void deletarRepactuacao(Long id) {
        if (!repactuacaoRepository.existsById(id)) {
            throw new RuntimeException("Repactuação não encontrada com ID: " + id);
        }
        repactuacaoRepository.deleteById(id);
    }

    /**
     * Método interno para aplicar repactuação automaticamente
     * Usado internamente pelos métodos criar e atualizar
     */
    @Transactional
    private void aplicarRepactuacaoInterna(Long idRepactuacao) {
        Repactuacao repactuacao = repactuacaoRepository.findById(idRepactuacao)
                .orElseThrow(() -> new RuntimeException("Repactuação não encontrada com ID: " + idRepactuacao));

        Contrato contrato = repactuacao.getIdContrato();

        System.out.println("Aplicando repactuação - Data da repactuação salva: " + repactuacao.getDtFimContrato());
        System.out.println("Data atual do contrato: " + contrato.getDtFim());

        // Atualiza a data fim do contrato com a nova data da repactuação
        contrato.setDtFim(repactuacao.getDtFimContrato());
        contrato.setDtAlteracao(new Date());

        // Salva o contrato com a nova data
        Contrato contratoSalvo = contratoRepository.save(contrato);

        System.out.println("Data aplicada no contrato: " + contratoSalvo.getDtFim());
    }

    /**
     * Método público para aplicar repactuação manualmente (mantido para compatibilidade)
     * Agora é opcional, pois a aplicação é automática
     */
    @Transactional
    public void aplicarRepactuacao(Long idRepactuacao) {
        aplicarRepactuacaoInterna(idRepactuacao);
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