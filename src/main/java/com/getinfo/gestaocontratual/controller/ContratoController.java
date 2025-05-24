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

@Tag(name = "Contratos", description = "Gerenciamento de contratos")
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
    private final ColaboradorRepository colaboradorRepository;
    private final ContratoColaboradorRepository contratoColaboradorRepository;
    private final EntregaveisColaboradorRepository entregaveisColaboradorRepository;

    @Autowired
    public ContratoController(ContratoRepository contratoRepository, ContratanteRepository contratanteRepository, StatusRepository statusRepository, DocumentoRepository documentoRepository, PostoTrabalhoRepository postoTrabalhoRepository, EntregaveisRepository entregaveisRepository, DocumentoService documentoService, ColaboradorRepository colaboradorRepository, ContratoColaboradorRepository contratoColaboradorRepository, EntregaveisColaboradorRepository entregaveisColaboradorRepository) {
        this.contratoRepository = contratoRepository;
        this.contratanteRepository = contratanteRepository;
        this.statusRepository = statusRepository;
        this.documentoRepository = documentoRepository;
        this.postoTrabalhoRepository = postoTrabalhoRepository;
        this.entregaveisRepository = entregaveisRepository;
        this.documentoService = documentoService;
        this.colaboradorRepository = colaboradorRepository;
        this.contratoColaboradorRepository = contratoColaboradorRepository;
        this.entregaveisColaboradorRepository = entregaveisColaboradorRepository;
    }

    @Operation(summary = "Cadastro de contrato")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/criarContrato", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> criarContrato(@RequestPart("contrato") CreateContratoRequest dto,
                                                @RequestPart(value = "documentos", required = false) MultipartFile[] documentos) {

        Contratante contratante = contratanteRepository.findById(dto.idContratante()).orElse(null);
        if (contratante == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Contratante não encontrado!");
        }

        if (dto.dtInicio() == null || dto.dtInicio().toString().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: A data de início não pode ser vazia.");
        }

        Status status = dto.idStatus() != null ? statusRepository.findById(dto.idStatus()).orElse(null) : null;

        Contrato contrato = new Contrato();
        contrato.setNumContrato(dto.numContrato());
        contrato.setDtFim(dto.dtFim());
        contrato.setDtInicio(dto.dtInicio());
        contrato.setIdContratante(contratante);
        contrato.setResponsavel(dto.responsavel());
        contrato.setStatus(status);
        contrato.setTipoContrato(dto.tipoContrato());
        contrato.setTipoServico(dto.tipoServico());

        if (dto.colaboradores() != null) {
            Set<Colaborador> colaboradoresSet = new HashSet<>();
            for (var c : dto.colaboradores()) {
                Optional<Colaborador> colaboradorOpt = colaboradorRepository.findById(c.id());
                if (colaboradorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Colaborador com ID " + c.id() + " não encontrado.");
                }
                colaboradoresSet.add(colaboradorOpt.get());
            }
            contrato.setColaboradores(colaboradoresSet);
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

        if (dto.postos() != null) {
            List<PostoTrabalho> postosList = new ArrayList<>();
            for (var p : dto.postos()) {
                if (p.nome() == null || p.nome().isBlank()) {
                    return ResponseEntity.badRequest().body("Erro: Nome do posto de trabalho é obrigatório.");
                }
                PostoTrabalho posto = new PostoTrabalho();
                posto.setNome(p.nome());
                posto.setDescricao(p.descricao());
                posto.setIdContrato(contrato);
                postosList.add(posto);
            }
            contrato.setPostos(postosList);
        }

        if (documentos != null && documentos.length > 0) {
            List<Documentos> documentosList = new ArrayList<>();
            for (MultipartFile file : documentos) {
                if (file != null && !file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    try {
                        documentoService.uploadFile(fileName, file);
                        String fileUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co/storage/v1/object/public/documentos/" + fileName;

                        Documentos documento = new Documentos();
                        documento.setNome(file.getOriginalFilename());
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

        List<PostoTrabalhoResponse> postos = contrato.getPostos().stream()
                .map(p -> new PostoTrabalhoResponse(
                        p.getId(),
                        p.getNome(),
                        p.getDescricao()
                ))
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
                contrato.getTipoContrato(),
                contrato.getTipoServico(),
                contrato.getResponsavel(),
                entregaveis,
                postos,
                documentos,
                contrato.getIdContratante(),
                colaboradores
        );
    }

    @Operation(summary = "Atualizar contrato")
    @Transactional(rollbackFor = Exception.class)
    @PutMapping(value = "/atualizarContrato/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> atualizarContrato(@PathVariable Long id,
                                                    @RequestPart("contrato") CreateContratoRequest dto,
                                                    @RequestPart(value = "documentos", required = false) MultipartFile[] documentos) {

        Optional<Contrato> contratoOpt = contratoRepository.findById(id);
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado para ID: " + id);
        }

        Contrato contrato = contratoOpt.get();

        Contratante contratante = contratanteRepository.findById(dto.idContratante()).orElse(null);
        if (contratante == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Contratante não encontrado!");
        }

        if (dto.dtInicio() == null || dto.dtInicio().toString().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: A data de início não pode ser vazia.");
        }

        Status status = dto.idStatus() != null ? statusRepository.findById(dto.idStatus()).orElse(null) : null;

        contrato.setNumContrato(dto.numContrato());
        contrato.setDtFim(dto.dtFim());
        contrato.setDtInicio(dto.dtInicio());
        contrato.setIdContratante(contratante);
        contrato.setResponsavel(dto.responsavel());
        contrato.setStatus(status);
        contrato.setTipoContrato(dto.tipoContrato());
        contrato.setTipoServico(dto.tipoServico());

        if (dto.colaboradores() != null) {
            Set<Colaborador> colaboradoresSet = new HashSet<>();
            for (var c : dto.colaboradores()) {
                Optional<Colaborador> colaboradorOpt = colaboradorRepository.findById(c.id());
                if (colaboradorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Colaborador com ID " + c.id() + " não encontrado.");
                }
                colaboradoresSet.add(colaboradorOpt.get());
            }
            contrato.setColaboradores(colaboradoresSet);
        } else {
            contrato.getColaboradores().clear();
        }

        if (dto.entregaveis() != null) {
            if (dto.entregaveis() == null) {
                System.out.println("Entregáveis é null");
            } else if (dto.entregaveis().isEmpty()) {
                System.out.println("Entregáveis está vazio");
            } else {
                System.out.println("Entregáveis tem: " + dto.entregaveis().size() + " elementos");
            }
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
                System.out.println("Status recebido: '" + e.Status() + "'");
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
        } else {
            contrato.getEntregaveis().clear();
        }

        if (dto.postos() != null) {
            List<PostoTrabalho> postosList = new ArrayList<>();
            for (var p : dto.postos()) {
                if (p.nome() == null || p.nome().isBlank()) {
                    return ResponseEntity.badRequest().body("Erro: Nome do posto de trabalho é obrigatório.");
                }
                PostoTrabalho posto = new PostoTrabalho();
                posto.setNome(p.nome());
                posto.setDescricao(p.descricao());
                posto.setIdContrato(contrato);
                postosList.add(posto);
            }
            contrato.setPostos(postosList);
        } else {
            contrato.getPostos().clear();
        }

        if (documentos != null && documentos.length > 0) {
            List<Documentos> documentosList = contrato.getDocumentos() != null
                    ? new ArrayList<>(contrato.getDocumentos())
                    : new ArrayList<>();

            for (MultipartFile file : documentos) {
                if (file != null && !file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    try {
                        documentoService.uploadFile(fileName, file);
                        String fileUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co/storage/v1/object/public/documentos/" + fileName;

                        Documentos documento = new Documentos();
                        documento.setNome(file.getOriginalFilename());
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
        } catch (Exception e) {
            if (contrato.getDocumentos() != null) {
                for (Documentos doc : contrato.getDocumentos()) {
                    try {
                        documentoService.deleteFile(doc.getUrl());
                    } catch (Exception ignored) { }
                }
            }
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
                contrato.getTipoContrato(),
                contrato.getTipoServico(),
                contrato.getResponsavel(),
                contrato.getIdContratante()
        );
    }


}
