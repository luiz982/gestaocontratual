package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.ContratoResponse;
import com.getinfo.gestaocontratual.controller.dto.CreateContratoRequest;
import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.*;
import com.getinfo.gestaocontratual.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Base64;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contratos")
public class ContratoController {
    private final ContratoRepository contratoRepository;
    private final ContratanteRepository contratanteRepository;
    private final StatusRepository statusRepository;
    private final DocumentoRepository documentoRepository;
    private final PostoTrabalhoRepository postoTrabalhoRepository;
    private final EntregaveisRepository entregaveisRepository;
    private final DocumentoService documentoService;

    @Autowired
    public ContratoController(ContratoRepository contratoRepository, ContratanteRepository contratanteRepository, StatusRepository statusRepository, DocumentoRepository documentoRepository, PostoTrabalhoRepository postoTrabalhoRepository, EntregaveisRepository entregaveisRepository, DocumentoService documentoService) {
        this.contratoRepository = contratoRepository;
        this.contratanteRepository = contratanteRepository;
        this.statusRepository = statusRepository;
        this.documentoRepository = documentoRepository;
        this.postoTrabalhoRepository = postoTrabalhoRepository;
        this.entregaveisRepository = entregaveisRepository;
        this.documentoService = documentoService;
    }

    @Operation(summary = "Cadastro de contrato")
    @Transactional
    @PostMapping(value = "/criarContrato", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> criarContrato(@RequestPart("contrato") CreateContratoRequest dto,
                                                @RequestPart(value = "documentos", required = false) MultipartFile[] documentos) {

        Contratante contratante = contratanteRepository.findById(dto.idContratante()).orElse(null);
        if (contratante == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Contratante não encontrado!");
        }

        if (dto.dtInicio() == null || dto.dtInicio().toString().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: A data de início não pode ser vazia.");
        }

        Status status = dto.idStatus() != null ? statusRepository.findById(dto.idStatus()).orElse(null) : null;

        Contrato contrato = new Contrato();
        contrato.setNumContrato(dto.numContrato());
        contrato.setDtFim(dto.dtFim());
        contrato.setDtInicio(dto.dtInicio());
        contrato.setDtAlteracao(dto.dtAlteracao());
        contrato.setIdContratante(contratante);
        contrato.setStatus(status);
        contrato.setTipoContrato(dto.tipoContrato());

        contrato = contratoRepository.save(contrato);

        if (dto.entregaveis() != null) {
            for (var e : dto.entregaveis()) {
                if (e.nome() == null || e.nome().isBlank()) {
                    return ResponseEntity.badRequest().body("Erro: Nome do entregável é obrigatório.");
                }

                if (e.dtInicio() == null) {
                    return ResponseEntity.badRequest().body("Erro: Data de início do entregável é obrigatória.");
                }

                try {
                    Entregaveis entregavel = new Entregaveis();
                    entregavel.setIdContrato(contrato);
                    entregavel.setNome(e.nome());
                    entregavel.setDtInicio(e.dtInicio());
                    entregavel.setDtFim(e.dtFim());

                    entregaveisRepository.save(entregavel);
                } catch (Exception ex) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao salvar entregável: " + ex.getMessage());
                }
            }
        }

        if (dto.postos() != null) {
            for (var p : dto.postos()) {
                if (p.nome() == null || p.nome().isBlank()) {
                    return ResponseEntity.badRequest().body("Erro: Nome do posto de trabalho é obrigatório.");
                }

                try {
                    PostoTrabalho posto = new PostoTrabalho();
                    posto.setIdContrato(contrato);
                    posto.setNome(p.nome());
                    posto.setDescricao(p.descricao());
                    postoTrabalhoRepository.save(posto);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao salvar posto de trabalho: " + e.getMessage());
                }
            }
        }

        if (documentos != null) {
            for (MultipartFile file : documentos) {
                if (file != null && !file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    try {
                        documentoService.uploadFile(fileName, file);
                        String fileUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co/storage/v1/object/public/" + "documentos/" + fileName;

                        Documentos documento = new Documentos();
                        documento.setContrato(contrato);
                        documento.setNome(file.getOriginalFilename());
                        documento.setUrl(fileUrl);
                        documentoRepository.save(documento);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erro ao fazer upload do documento para o Supabase Storage: " + e.getMessage());
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Contrato cadastrado com sucesso. ID:" + contrato.getIdContrato());
    }

    @Operation(summary = "Retorna todos os contratos cadastrados")
    @GetMapping("/contratos")
    public ResponseEntity<List<ContratoResponse>> contratos() {
        List<Contrato> contratos = contratoRepository.findAll();
        List<ContratoResponse> contratosComDetalles = contratos.stream()
                .map(this::getContratoComDetalhes)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contratosComDetalles);
    }

    @Operation(summary = "Retorna todos os contratos arquivados")
    @GetMapping("/listarArquivados")
    public ResponseEntity<List<ContratoResponse>> contratosArquivados() {
        List<Contrato> contratos = contratoRepository.findByStatus_NomeIgnoreCase("arquivado");
        List<ContratoResponse> contratosComDetalles = contratos.stream()
                .map(this::getContratoComDetalhes)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contratosComDetalles);
    }

    @Operation(summary = "Retorna todos os contratos ativos")
    @GetMapping("/listarAtivos")
    public ResponseEntity<List<ContratoResponse>> contratosAtivos() {
        List<Contrato> contratos = contratoRepository.findAllAtivos();
        List<ContratoResponse> contratosComDetalles = contratos.stream()
                .map(this::getContratoComDetalhes)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contratosComDetalles);
    }

    @Operation(summary = "Retorna um contrato pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarContratoPorId(@PathVariable Long id) {
        Optional<Contrato> contrato = contratoRepository.findById(id);
        if (contrato.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Contrato não encontrado!");
        }
        ContratoResponse contratoComDetalles = getContratoComDetalhes(contrato.get());
        return ResponseEntity.ok(contratoComDetalles);
    }

    private ContratoResponse getContratoComDetalhes(Contrato contrato) {
        contrato.getIdContratante();
        contrato.getStatus();

        List<Entregaveis> entregaveis = entregaveisRepository.findByIdContrato_IdContrato(contrato.getIdContrato());
        List<PostoTrabalho> postosTrabalho = postoTrabalhoRepository.findByIdContrato_IdContrato(contrato.getIdContrato());
        List<Documentos> documentos = documentoRepository.findByContratoId(contrato.getIdContrato());

        ContratoResponse contratoResponse = new ContratoResponse();
        contratoResponse.setIdContrato(contrato.getIdContrato());
        contratoResponse.setNumContrato(contrato.getNumContrato());
        contratoResponse.setDtInicio(contrato.getDtInicio());
        contratoResponse.setDtFim(contrato.getDtFim());
        contratoResponse.setDtAlteracao(contrato.getDtAlteracao());
        contratoResponse.setIdContratante(contrato.getIdContratante() != null ? contrato.getIdContratante().getIdContratante() : null);
        contratoResponse.setStatus(contrato.getStatus() != null ? contrato.getStatus().getIdStatus() : null);
        contratoResponse.setTipoContrato(contrato.getTipoContrato());
        contratoResponse.setEntregaveis(entregaveis);
        contratoResponse.setPostosTrabalho(postosTrabalho);
        contratoResponse.setDocumentos(documentos);

        return contratoResponse;
    }

    @Operation(summary = "Atualiza um contrato")
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarContrato(@PathVariable Long id,
                                               @RequestPart("contrato") CreateContratoRequest dto,
                                               @RequestPart(value = "documentos", required = false) MultipartFile[] documentos) {
        Optional<Contrato> contratoOptional = contratoRepository.findById(id);
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }

        Contrato contrato = contratoOptional.get();

        Contratante contratante = contratanteRepository.findById(dto.idContratante()).orElse(null);
        if (contratante == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Contratante não encontrado!");
        }

        if (dto.dtInicio() == null || dto.dtInicio().toString().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: A data de início não pode ser vazia.");
        }

        Status status = dto.idStatus() != null ? statusRepository.findById(dto.idStatus()).orElse(null) : null;

        contrato.setNumContrato(dto.numContrato());
        contrato.setDtFim(dto.dtFim());
        contrato.setDtInicio(dto.dtInicio());
        contrato.setDtAlteracao(dto.dtAlteracao());
        contrato.setIdContratante(contratante);
        contrato.setStatus(status);
        contrato.setTipoContrato(dto.tipoContrato());

        contrato = contratoRepository.save(contrato);

        if (id != null) entregaveisRepository.deleteAllByIdContrato_IdContrato(contrato.getIdContrato());

        if (dto.entregaveis() != null) {
            for (var e : dto.entregaveis()) {
                if (e.nome() == null || e.nome().isBlank()) {
                    return ResponseEntity.badRequest().body("Erro: Nome do entregável é obrigatório.");
                }

                if (e.dtInicio() == null) {
                    return ResponseEntity.badRequest().body("Erro: Data de início do entregável é obrigatória.");
                }

                try {
                    Entregaveis entregavel = new Entregaveis();
                    entregavel.setIdContrato(contrato);
                    entregavel.setNome(e.nome());
                    entregavel.setDtInicio(e.dtInicio());
                    entregavel.setDtFim(e.dtFim());

                    entregaveisRepository.save(entregavel);
                } catch (Exception ex) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao salvar entregável: " + ex.getMessage());
                }
            }
        }

        if (id != null) postoTrabalhoRepository.deleteAllByIdContrato_IdContrato(contrato.getIdContrato());

        if (dto.postos() != null) {
            for (var p : dto.postos()) {
                if (p.nome() == null || p.nome().isBlank()) {
                    return ResponseEntity.badRequest().body("Erro: Nome do posto de trabalho é obrigatório.");
                }

                try {
                    PostoTrabalho posto = new PostoTrabalho();
                    posto.setIdContrato(contrato);
                    posto.setNome(p.nome());
                    posto.setDescricao(p.descricao());
                    postoTrabalhoRepository.save(posto);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao salvar posto de trabalho: " + e.getMessage());
                }
            }
        }

        if (documentos != null) {
            List<Documentos> documentosExistentes = documentoRepository.findByContratoId(contrato.getIdContrato());
            for (Documentos doc : documentosExistentes) {
                documentoService.deleteFile(doc.getUrl().substring(doc.getUrl().lastIndexOf('/') + 1));
                documentoRepository.deleteById(doc.getIdDocumento());
            }

            for (MultipartFile file : documentos) {
                if (file != null && !file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    try {
                        documentoService.uploadFile(fileName, file);
                        String fileUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co/storage/v1/object/public/" + "documentos/" + fileName;

                        Documentos novoDocumento = new Documentos();
                        novoDocumento.setContrato(contrato);
                        novoDocumento.setNome(file.getOriginalFilename());
                        novoDocumento.setUrl(fileUrl);
                        documentoRepository.save(novoDocumento);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erro ao fazer upload do documento para o Supabase Storage: " + e.getMessage());
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("Contrato atualizado com sucesso. ID:" + contrato.getIdContrato());
    }


    @Operation(summary = "Deleta um contrato")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarContrato(@PathVariable Long id) {
        Optional<Contrato> contratoOpt = contratoRepository.findById(id);
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }

        try {
            List<Documentos> documentos = documentoRepository.findByContratoId(id);
            for (Documentos doc : documentos) {
                documentoService.deleteFile(doc.getUrl().substring(doc.getUrl().lastIndexOf('/') + 1));
                documentoRepository.deleteById(doc.getIdDocumento());
            }

            entregaveisRepository.deleteAllByIdContrato_IdContrato(id);
            postoTrabalhoRepository.deleteAllByIdContrato_IdContrato(id);
            contratoRepository.deleteById(id);

            return ResponseEntity.ok("Contrato deletado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar contrato: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualiza apenas o status do contrato")
    @Transactional
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusContrato(@PathVariable Long id, @RequestParam Long idStatus) {
        Optional<Contrato> contratoOptional = contratoRepository.findById(id);
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }

        Status novoStatus = statusRepository.findById(idStatus).orElse(null);
        if (novoStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status não encontrado");
        }

        Contrato contrato = contratoOptional.get();
        contrato.setStatus(novoStatus);
        contratoRepository.save(contrato);

        return ResponseEntity.ok("Status do contrato atualizado para: " + novoStatus.getNome());
    }

    @Operation(summary = "Arquiva o contrato (status: Arquivado)")
    @PatchMapping("/{id}/arquivar")
    @Transactional
    public ResponseEntity<?> arquivarContrato(@PathVariable Long id) {
        Optional<Contrato> contratoOpt = contratoRepository.findById(id);
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado.");
        }

        Status statusArquivado = statusRepository.findByNomeIgnoreCase("Arquivado");

        if (statusArquivado == null) {
            statusArquivado = new Status();
            statusArquivado.setNome("Arquivado");
            statusArquivado.setDescricao("Contrato arquivado");
            statusRepository.save(statusArquivado);
        }

        Contrato contrato = contratoOpt.get();
        contrato.setStatus(statusArquivado);
        contratoRepository.save(contrato);

        return ResponseEntity.ok("Contrato arquivado com sucesso.");
    }

    @Operation(summary = "Desarquivar o contrato (status: Ativo)")
    @PatchMapping("/{id}/desarquivar")
    @Transactional
    public ResponseEntity<?> desarquivarContrato(@PathVariable Long id) {
        Optional<Contrato> contratoOpt = contratoRepository.findById(id);
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado.");
        }

        Status statusAtivo = statusRepository.findByNomeIgnoreCase("Ativo");

        if (statusAtivo == null) {
            statusAtivo = new Status();
            statusAtivo.setNome("Ativo");
            statusAtivo.setDescricao("Contrato ativo");
            statusRepository.save(statusAtivo);
        }

        Contrato contrato = contratoOpt.get();
        contrato.setStatus(statusAtivo);
        contratoRepository.save(contrato);

        return ResponseEntity.ok("Contrato desarquivado com sucesso.");
    }
}
