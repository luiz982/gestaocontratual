package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.*;
import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "Entregáveis", description = "Gerenciamento de entregáveis")
@RestController
@RequestMapping("/entregaveis")
public class EntregaveisController {

    private final EntregaveisRepository EntregaveisRepository;
    private final ContratoRepository contratoRepository;
    private final EntregaveisColaboradorRepository entregaveisColaboradorRepository;
    private final ColaboradorRepository colaboradorRepository;

    public EntregaveisController(EntregaveisRepository entregaveisRepository,
                                 ContratoRepository contratoRepository, EntregaveisColaboradorRepository entregaveisColaboradorRepository,
                                 ColaboradorRepository colaboradorRepository) {

        this.EntregaveisRepository = entregaveisRepository;
        this.contratoRepository = contratoRepository;
        this.entregaveisColaboradorRepository = entregaveisColaboradorRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

//    @GetMapping
//    public ResponseEntity<List<ResponseEntregaveisFindAll>> getEntregaveis() {
//        List<Entregaveis> entregaveisList = entregaveisRepository.findAll();
//
//        List<ResponseEntregaveisFindAll> responseList = entregaveisList.stream()
//                .map(entregavel -> new ResponseEntregaveisFindAll(
//                        entregavel.getIdEntregavel(),
//                        entregavel.getIdContrato() != null ? entregavel.getIdContrato().getIdContrato() : null,
//                        entregavel.getNome(),
//                        entregavel.getDtInicio(),
//                        entregavel.getDtFim(),
//                        entregavel.getStatus(),
//                        entregavel.getDescricao(),
//                        entregavel.getColaboradores().stream()
//                                .map(ec -> new ColaboradorResponse(
//                                        ec.getColaborador().getId(),
//                                        ec.getColaborador().getCpf(),
//                                        ec.getColaborador().getNome(),
//                                        ec.getColaborador().getCargo(),
//                                        ec.getColaborador().isSituacao(),
//                                        rel.getFuncaoEntregavel()
//                                ))
//                                .toList()
//                ))
//                .toList();
//
//        return ResponseEntity.ok(responseList);
//    }

    @Operation(summary = "Retorna todos os entregáveis de um contrato")
    @GetMapping("/contrato/{idContrato}")
    public ResponseEntity<?> entregaveisPorContrato(@PathVariable Long idContrato) {
        List<Entregaveis> listaEntregaveis = EntregaveisRepository.findByIdContrato_IdContrato(idContrato);
    
        if (listaEntregaveis.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Nenhum entregável encontrado para este contrato.");
        }
        
        List<EntregaveisResponse> listaEntregaveisDetalhe = new ArrayList<>();
        for (Entregaveis entregavel : listaEntregaveis) {
                    List<EntregaveisColaborador> colaboradores = entregaveisColaboradorRepository.findByEntregavel_IdEntregavel(entregavel.getIdEntregavel());
                            List<ColaboradorResponse> colaboradoresEntregaveisResponse = colaboradores.stream()
                            .map(rel -> {
                                Colaborador c = rel.getColaborador();
                                return new ColaboradorResponse(
                                        c.getId(),
                                        c.getCpf(),
                                        c.getNome(),
                                        c.getCargo(),
                                        c.isSituacao(),
                                        rel.getFuncaoEntregavel()
                                );
                            }).toList();
        
                    EntregaveisResponse resposta = new EntregaveisResponse(
                            entregavel.getIdEntregavel(),
                            entregavel.getNome(),
                            entregavel.getDtInicio(),
                            entregavel.getDtFim(),                    
                            entregavel.getStatus(),
                            entregavel.getDescricao(),
                            colaboradoresEntregaveisResponse
                    );
                    listaEntregaveisDetalhe.add(resposta);
                }

       
    
        return ResponseEntity.ok(listaEntregaveisDetalhe);
    }


    @PostMapping("/criarEntregaveis")
    public ResponseEntity<String> CriarEntregaveis(@RequestBody CreateEntregaveisRequest dto) {

        if (dto.idContrato() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Numero do Contrato não informado!");
        }

        if (dto.nome() == null || dto.nome().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Nome do Entregável não informado!");
        }

        if (dto.dtInicio() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Data de Início não informada!");
        }

        if (dto.dtFim() != null && dto.dtInicio().after(dto.dtFim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Data de Início maior que a Data de Fim!");
        }

        Optional<Contrato> contratoOptional = contratoRepository.findById(dto.idContrato());
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }

        Entregaveis entregaveis = new Entregaveis();

        entregaveis.setColaboradores(new ArrayList<>());

        if (dto.colaboradores() != null && !dto.colaboradores().isEmpty()) {
            for (EntregavelColaboradorRequest c : dto.colaboradores()) {
                Optional<Colaborador> colaboradorOpt = colaboradorRepository.findById(c.id());
                if (colaboradorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Colaborador com ID " + c.id() + " não encontrado.");
                }
                EntregaveisColaborador relacao = new EntregaveisColaborador();
                relacao.setColaborador(colaboradorOpt.get());
                relacao.setEntregavel(entregaveis);
                relacao.setFuncaoEntregavel(c.funcaoEntregavel());

                entregaveis.getColaboradores().add(relacao);
            }
        }

        entregaveis.setIdContrato(contratoOptional.get());
        entregaveis.setNome(dto.nome());
        entregaveis.setDtInicio(dto.dtInicio());
        entregaveis.setDtFim(dto.dtFim());
        entregaveis.setStatus(dto.Status());
        entregaveis.setDescricao(dto.descricao());

        EntregaveisRepository.save(entregaveis);

        return ResponseEntity.ok("Entregável criado com sucesso");
    }


    @PutMapping("/atualizarEntregaveis/{id}")
    @Transactional
    public ResponseEntity<String> atualizarEntregaveis(@PathVariable Long id, @RequestBody CreateEntregaveisRequest dto) {

        Optional<Entregaveis> entregaveisOptional = EntregaveisRepository.findById(id);
        if (entregaveisOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entregável não encontrado.");
        }

        if (dto.idContrato() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Numero do Contrato não informado!");
        }

        if (dto.nome() == null || dto.nome().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Nome do Entregável não informado!");
        }

        if (dto.dtInicio() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Data de Início não informada!");
        }

        if (dto.dtFim() != null && dto.dtInicio().after(dto.dtFim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Data de Início maior que a Data de Fim!");
        }

        Optional<Contrato> contratoOptional = contratoRepository.findById(dto.idContrato());
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }

        Entregaveis entregaveis = entregaveisOptional.get();

        entregaveis.setIdContrato(contratoOptional.get());
        entregaveis.setNome(dto.nome());
        entregaveis.setDtInicio(dto.dtInicio());
        entregaveis.setDtFim(dto.dtFim());
        entregaveis.setStatus(dto.Status());
        entregaveis.setDescricao(dto.descricao());

        entregaveis.getColaboradores().clear();

        if (dto.colaboradores() != null && !dto.colaboradores().isEmpty()) {
            for (EntregavelColaboradorRequest c : dto.colaboradores()) {
                Optional<Colaborador> colaboradorOpt = colaboradorRepository.findById(c.id());
                if (colaboradorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Colaborador com ID " + c.id() + " não encontrado.");
                }
                EntregaveisColaborador relacao = new EntregaveisColaborador();
                relacao.setColaborador(colaboradorOpt.get());
                relacao.setEntregavel(entregaveis);
                relacao.setFuncaoEntregavel(c.funcaoEntregavel());

                entregaveis.getColaboradores().add(relacao);
            }
        }

        EntregaveisRepository.save(entregaveis);

        return ResponseEntity.ok("Entregável atualizado com sucesso.");
    }

    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity<String> DeletarEntregaveis(@PathVariable("id") Long idEntregavel)
    {
        Entregaveis entregaveis = EntregaveisRepository.findById(idEntregavel)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entregaveis não encontrado"));

        EntregaveisRepository.delete(entregaveis);

        return ResponseEntity.ok("Entregaveis deletado com sucesso");
    }

    
        

    
}
