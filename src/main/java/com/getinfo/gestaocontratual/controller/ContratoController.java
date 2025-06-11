package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.*;
import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.*;
import com.getinfo.gestaocontratual.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.getinfo.gestaocontratual.utils.Validadores.sanitizeFileName;

@Tag(name = "Contratos", description = "Gerenciamento de contratos")
@RestController
@RequestMapping("/contratos")
public class ContratoController {
    private final ContratoRepository contratoRepository;
    private final ContratanteRepository contratanteRepository;
    private final StatusRepository statusRepository;
    private final DocumentoService documentoService;
    private final ColaboradorRepository colaboradorRepository;

    @Autowired
    public ContratoController(ContratoRepository contratoRepository, ContratanteRepository contratanteRepository, StatusRepository statusRepository, DocumentoRepository documentoRepository, EntregaveisRepository entregaveisRepository, DocumentoService documentoService, ColaboradorRepository colaboradorRepository, ContratoColaboradorRepository contratoColaboradorRepository, EntregaveisColaboradorRepository entregaveisColaboradorRepository) {
        this.contratoRepository = contratoRepository;
        this.contratanteRepository = contratanteRepository;
        this.statusRepository = statusRepository;
        this.documentoService = documentoService;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Operation(summary = "Cadastro de contrato")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/criarContrato", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> criarContrato(
            @RequestPart("contrato") CreateContratoRequest dto,
            @RequestPart(value = "documentos", required = false) MultipartFile[] documentos) {

        Contratante contratante = contratanteRepository.findById(dto.idContratante()).orElse(null);
        if (contratante == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Contratante não encontrado!");
        }

        if (dto.dtInicio() == null || dto.dtInicio().toString().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: A data de início não pode ser vazia.");
        }

        Status status = statusRepository.findByNomeIgnoreCase("Ativo");
        if (status == null) {
            status = new Status();
            status.setNome("Ativo");
            status.setDescricao("Contrato ativo!");
            statusRepository.save(status);
        }

        Contrato contrato = new Contrato();
        contrato.setDtFim(dto.dtFim());
        contrato.setDtInicio(dto.dtInicio());
        contrato.setIdContratante(contratante);
        contrato.setResponsavel(dto.responsavel());
        contrato.setStatus(status);
        contrato.setTipoServico(dto.tipoServico());


        System.out.println(dto.colaboradores());
        if (dto.colaboradores() != null) {
            List<ContratoColaborador> contratoColaboradores = new ArrayList<>();

            for (var c : dto.colaboradores()) {
                Optional<Colaborador> colaboradorOpt = colaboradorRepository.findById(c.id());
                if (colaboradorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Colaborador com ID " + c.id() + " não encontrado.");
                }

                ContratoColaborador relacao = new ContratoColaborador();
                relacao.setContrato(contrato);
                relacao.setColaborador(colaboradorOpt.get());
                relacao.setFuncaoContrato(c.funcaoContrato());

                contratoColaboradores.add(relacao);
            }

            contrato.setContratoColaboradores(contratoColaboradores);
        }

        if (dto.entregaveis() != null && dto.entregaveis().toArray().length != 0) {
            List<Entregaveis> entregaveisList = new ArrayList<>();
            for (var e : dto.entregaveis()) {
                if (e.nome() == null || e.nome().isBlank()) {
                    return ResponseEntity.badRequest().body("Erro: Nome do entregável é obrigatório.");
                }
                if (e.dtInicio() == null) {
                    return ResponseEntity.badRequest().body("Erro: Data de início do entregável é obrigatória.");
                }
                if (e.dtFim() != null && e.dtInicio().after(e.dtFim())) {
                    return ResponseEntity.badRequest().body("Erro: Data de início maior que a data de fim no entregável: " + e.nome());
                }

                Entregaveis entregavel = new Entregaveis();
                entregavel.setNome(e.nome());
                entregavel.setDtInicio(e.dtInicio());
                entregavel.setDtFim(e.dtFim());

                if (e.Status() != null && !e.Status().isBlank() && !e.Status().isEmpty()) {
                    try {
                        entregavel.setStatus(StatusEntregavel.valueOf(e.Status()));
                    } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest()
                                .body("Erro: Status inválido para o entregável: " + e.nome());
                    }
                } else {
                    return ResponseEntity.badRequest()
                            .body("Erro: Status inválido para o entregável: " + e.nome());
                }

                entregavel.setDescricao(e.descricao());
                entregavel.setIdContrato(contrato);

                if (e.colaboradores() != null && !e.colaboradores().isEmpty()) {
                    List<EntregaveisColaborador> entregaveisColaboradores = new ArrayList<>();
                    for (var c : e.colaboradores()) {
                        Optional<Colaborador> colaboradorOpt = colaboradorRepository.findById(c.id());
                        if (colaboradorOpt.isEmpty()) {
                            return ResponseEntity.badRequest()
                                    .body("Colaborador com ID " + c.id() + " não encontrado para o entregável " + e.nome());
                        }
                        EntregaveisColaborador relacao = new EntregaveisColaborador();
                        relacao.setColaborador(colaboradorOpt.get());
                        relacao.setFuncaoEntregavel(c.funcaoEntregavel());
                        relacao.setEntregavel(entregavel);
                        entregaveisColaboradores.add(relacao);
                    }
                    entregavel.setColaboradores(entregaveisColaboradores);
                }

                entregaveisList.add(entregavel);
            }
            contrato.setEntregaveis(entregaveisList);
        }

        if (documentos != null && documentos.length > 0) {
            List<Documentos> documentosList = new ArrayList<>();
            for (MultipartFile file : documentos) {
                if (file != null && !file.isEmpty()) {
                    String sanitizedOriginalFileName = sanitizeFileName(file.getOriginalFilename());
                    String fileName = UUID.randomUUID().toString() + "_" + sanitizedOriginalFileName;

                    try {
                        documentoService.uploadFile(fileName, file);
                        String fileUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co/storage/v1/object/public/documentos/" + fileName;

                        Documentos documento = new Documentos();
                        documento.setNome(sanitizeFileName(file.getOriginalFilename()));
                        documento.setUrl(fileUrl);
                        documento.setContrato(contrato);

                        documentosList.add(documento);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erro ao fazer upload do documento: " + e.getMessage());
                    }
                }
            }
            contrato.setDocumentos(documentosList);
        }

        try {
            contratoRepository.save(contrato);
            contrato.setNumContrato(contrato.getIdContrato());
            contratoRepository.save(contrato);
        } catch (Exception e) {
            if (contrato.getDocumentos() != null) {
                for (Documentos doc : contrato.getDocumentos()) {
                    try {
                        String fileName = doc.getUrl().substring(doc.getUrl().lastIndexOf('/') + 1);
                        documentoService.deleteFile(fileName);
                    } catch (Exception ignored) { }
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar o contrato: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Contrato cadastrado com sucesso. ID: " + contrato.getIdContrato());
    }


    @Operation(summary = "Retorna todos os contratos cadastrados")
    @GetMapping("/contratos")
    public ResponseEntity<List<ContratoDataTable>> contratos() {
        List<Contrato> contratos = contratoRepository.findAll();
        List<ContratoDataTable> allContratos = contratos.stream()
                .map(this::getAllContrato)
                .collect(Collectors.toList());
        return ResponseEntity.ok(allContratos);
    }

    @Operation(summary = "Retorna todos os contratos arquivados")
    @GetMapping("/listarArquivados")
    public ResponseEntity<List<ContratoDataTable>> contratosArquivados() {
        List<Contrato> contratos = contratoRepository.findByStatus_NomeIgnoreCase("arquivado");
        List<ContratoDataTable> allContratos = contratos.stream()
                .map(this::getAllContrato)
                .collect(Collectors.toList());
        return ResponseEntity.ok(allContratos);
    }

    @Operation(summary = "Retorna todos os contratos ativos")
    @GetMapping("/listarAtivos")
    public ResponseEntity<List<ContratoDataTable>> contratosAtivos() {
        List<Contrato> contratos = contratoRepository.findAllAtivos();
        List<ContratoDataTable> allContratos = contratos.stream()
                .map(this::getAllContrato)
                .collect(Collectors.toList());
        return ResponseEntity.ok(allContratos);
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
        List<ColaboradorResponse> colaboradores = contrato.getContratoColaboradores().stream()
                .map(cc -> new ColaboradorResponse(
                        cc.getColaborador().getId(),
                        cc.getColaborador().getCpf(),
                        cc.getColaborador().getNome(),
                        cc.getColaborador().getCargo(),
                        cc.getColaborador().isSituacao(),
                        cc.getFuncaoContrato()
                ))
                .toList();

        List<EntregaveisResponse> entregaveis = contrato.getEntregaveis().stream()
                .map(e -> {
                    List<ColaboradorResponse> colabsEntregavel = e.getColaboradores().stream()
                            .map(ec -> new ColaboradorResponse(
                                    ec.getColaborador().getId(),
                                    ec.getColaborador().getCpf(),
                                    ec.getColaborador().getNome(),
                                    ec.getColaborador().getCargo(),
                                    ec.getColaborador().isSituacao(),
                                    ec.getFuncaoEntregavel()
                            ))
                            .toList();

                    return new EntregaveisResponse(
                            e.getIdEntregavel(),
                            e.getNome(),
                            e.getDtInicio(),
                            e.getDtFim(),
                            e.getStatus(),
                            e.getDescricao(),
                            colabsEntregavel
                    );
                })
                .toList();

        List<DocumentoResponse> documentos = contrato.getDocumentos().stream()
                .map(d -> new DocumentoResponse(
                        d.getIdDocumento(),
                        d.getNome(),
                        d.getUrl()
                ))
                .toList();

        return new ContratoResponse(
                contrato.getIdContrato(),
                contrato.getNumContrato(),
                contrato.getDtInicio(),
                contrato.getDtFim(),
                contrato.getDtAlteracao(),
                contrato.getIdContratante().getIdContratante(),
                contrato.getStatus().getNome(),
                contrato.getTipoServico(),
                contrato.getResponsavel(),
                entregaveis,
                documentos,
                contrato.getIdContratante(),
                colaboradores
        );
    }

    @Operation(summary = "Atualizar contrato")
    @Transactional(rollbackFor = Exception.class)
    @PutMapping(value = "/atualizarContrato/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> atualizarContrato(@PathVariable Long id,
                                                    @RequestPart("contrato") AlteraContratoRequest dto,
                                                    @RequestPart(value = "documentos", required = false) MultipartFile[] documentos) {
        Optional<Contrato> contratoOpt = contratoRepository.findById(id);
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Contrato não encontrado para ID: " + id);
        }

        Contrato contrato = contratoOpt.get();

        Contratante contratante = contratanteRepository.findById(dto.idContratante()).orElse(null);
        if (contratante == null) {
            return ResponseEntity.badRequest().body("Erro: Contratante não encontrado para ID: " + dto.idContratante());
        }

        if (dto.dtInicio() == null) {
            return ResponseEntity.badRequest().body("Erro: A data de início não pode ser nula.");
        }

        Status status;
        String nomeStatus = dto.status();

        if (nomeStatus != null && !nomeStatus.isBlank()) {
            status = statusRepository.findByNomeIgnoreCase(nomeStatus);

            if (status == null) {
                return ResponseEntity.badRequest()
                        .body("Erro: Status com nome '" + nomeStatus + "' não encontrado!");
            }
        } else {
            return ResponseEntity.badRequest()
                    .body("Erro: Status do contrato não pode ser nulo ou vazio!");
        }

        contrato.setDtInicio(dto.dtInicio());
        contrato.setDtFim(dto.dtFim());
        contrato.setResponsavel(dto.responsavel());
        contrato.setTipoServico(dto.tipoServico());
        contrato.setIdContratante(contratante);
        contrato.setStatus(status);

        contrato.getContratoColaboradores().clear();
        if (dto.colaboradores() != null) {
            for (var c : dto.colaboradores()) {
                Optional<Colaborador> colaboradorOpt = colaboradorRepository.findById(c.id());
                if (colaboradorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Erro: Colaborador com ID " + c.id() + " não encontrado.");
                }
                ContratoColaborador relacao = new ContratoColaborador();
                relacao.setColaborador(colaboradorOpt.get());
                relacao.setContrato(contrato);
                relacao.setFuncaoContrato(c.funcaoContrato());
                contrato.getContratoColaboradores().add(relacao);
            }
        }

        contrato.getEntregaveis().clear();
        if (dto.entregaveis() != null) {
            for (var e : dto.entregaveis()) {
                if (e.nome() == null || e.nome().isBlank()) {
                    return ResponseEntity.badRequest().body("Erro: Nome do entregável é obrigatório.");
                }
                if (e.dtInicio() == null) {
                    return ResponseEntity.badRequest().body("Erro: Data de início do entregável é obrigatória.");
                }
                if (e.dtFim() != null && e.dtInicio().after(e.dtFim())) {
                    return ResponseEntity.badRequest().body("Erro: Data de início maior que a data de fim no entregável: " + e.nome());
                }

                Entregaveis entregavel = new Entregaveis();
                entregavel.setIdContrato(contrato);
                entregavel.setNome(e.nome());
                entregavel.setDtInicio(e.dtInicio());
                entregavel.setDtFim(e.dtFim());
                entregavel.setDescricao(e.descricao());
                System.out.println(e.Status());
                if (e.Status() != null && !e.Status().isBlank()) {
                    try {
                        entregavel.setStatus(StatusEntregavel.valueOf(e.Status()));
                    } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest().body("Erro: Status inválido para o entregável: " + e.nome());
                    }
                } else {
                    return ResponseEntity.badRequest().body("Erro: Status é obrigatório para o entregável: " + e.nome());
                }

                List<EntregaveisColaborador> relacoes = new ArrayList<>();
                if (e.colaboradores() != null) {
                    for (var c : e.colaboradores()) {
                        Optional<Colaborador> colabOpt = colaboradorRepository.findById(c.id());
                        if (colabOpt.isEmpty()) {
                            return ResponseEntity.badRequest().body("Erro: Colaborador com ID " + c.id() + " não encontrado no entregável: " + e.nome());
                        }
                        EntregaveisColaborador relacao = new EntregaveisColaborador();
                        relacao.setColaborador(colabOpt.get());
                        relacao.setFuncaoEntregavel(c.funcaoEntregavel());
                        relacao.setEntregavel(entregavel);
                        relacoes.add(relacao);
                    }
                }

                entregavel.setColaboradores(relacoes);
                contrato.getEntregaveis().add(entregavel);
            }
        }

        contrato.getDocumentos().clear();

        if (documentos != null && documentos.length > 0) {
            for (MultipartFile file : documentos) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String cleanOriginalName = sanitizeFileName(file.getOriginalFilename());
                        String fileName = UUID.randomUUID().toString() + "_" + cleanOriginalName;
                        documentoService.uploadFile(fileName, file);
                        String fileUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co/storage/v1/object/public/documentos/" + fileName;

                        Documentos doc = new Documentos();
                        doc.setNome(cleanOriginalName);
                        doc.setUrl(fileUrl);
                        doc.setContrato(contrato);

                        contrato.getDocumentos().add(doc);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erro ao fazer upload do documento: " + e.getMessage());
                    }
                }
            }
        }

        try {
            contratoRepository.save(contrato);
            contrato.setNumContrato(contrato.getIdContrato());
            contratoRepository.save(contrato);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o contrato: " + e.getMessage());
        }

        return ResponseEntity.ok("Contrato atualizado com sucesso. ID: " + contrato.getIdContrato());
    }



    @Operation(summary = "Deleta um contrato")
    @Transactional(rollbackFor = Exception.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarContrato(@PathVariable Long id) {
        Optional<Contrato> contratoOpt = contratoRepository.findById(id);

        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }

        Contrato contrato = contratoOpt.get();

        try {
            if (contrato.getDocumentos() != null) {
                for (Documentos doc : contrato.getDocumentos()) {
                    try {
                        documentoService.deleteFile(doc.getUrl());
                    } catch (Exception e) {
                        System.err.println("Erro ao deletar arquivo no storage: " + e.getMessage());
                    }
                }
            }

            contratoRepository.delete(contrato);

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

    private ContratoDataTable getAllContrato(Contrato contrato) {
        return new ContratoDataTable(
                contrato.getIdContrato(),
                contrato.getNumContrato(),
                contrato.getDtInicio(),
                contrato.getDtFim(),
                contrato.getDtAlteracao(),
                contrato.getIdContratante().getIdContratante(),
                contrato.getStatus().getNome(),
                contrato.getTipoServico(),
                contrato.getResponsavel(),
                contrato.getIdContratante()
        );
    }


}
