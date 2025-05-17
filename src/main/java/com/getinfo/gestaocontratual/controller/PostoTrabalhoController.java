package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.CreatePostoTrabalhoRequest;
import com.getinfo.gestaocontratual.controller.dto.PostoTrabalhoResponse;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.PostoTrabalho;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import com.getinfo.gestaocontratual.repository.PostoTrabalhoRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/postos-trabalho")
public class PostoTrabalhoController {

    private final PostoTrabalhoRepository postoRepository;
    private final ContratoRepository contratoRepository;

    public PostoTrabalhoController(PostoTrabalhoRepository postoRepository, ContratoRepository contratoRepository) {
        this.postoRepository = postoRepository;
        this.contratoRepository = contratoRepository;
    }

    @Operation(summary = "Criar novo posto de trabalho vinculado a um contrato")
    @PostMapping
    @Transactional
    public ResponseEntity<?> criarPosto(@RequestBody CreatePostoTrabalhoRequest dto) {
        Optional<Contrato> contratoOpt = contratoRepository.findById(dto.idContrato());
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erro: Contrato não encontrado.");
        }

        PostoTrabalho posto = new PostoTrabalho();
        posto.setIdContrato(contratoOpt.get());
        posto.setNome(dto.nome());
        posto.setDescricao(dto.descricao());

        postoRepository.save(posto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Posto de trabalho criado com sucesso.");
    }

    @Operation(summary = "Listar postos de trabalho de um contrato")
    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<PostoTrabalhoResponse>> listarPorContrato(@PathVariable Long contratoId) {
        List<PostoTrabalhoResponse> response = postoRepository.findByIdContrato_IdContrato(contratoId).stream()
                .map(p -> new PostoTrabalhoResponse(
                        p.getId(),
                        p.getIdContrato().getIdContrato(),
                        p.getNome(),
                        p.getDescricao()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar um posto de trabalho por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<PostoTrabalho> posto = postoRepository.findById(id);
        if (posto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Posto de trabalho não encontrado.");
        }

        PostoTrabalho p = posto.get();
        return ResponseEntity.ok(new PostoTrabalhoResponse(
                p.getId(),
                p.getIdContrato().getIdContrato(),
                p.getNome(),
                p.getDescricao()
        ));
    }



    @Operation(summary = "Atualizar um posto de trabalho existente")
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> atualizarPosto(@PathVariable Long id, @RequestBody CreatePostoTrabalhoRequest dto) {
        Optional<PostoTrabalho> postoOptional = postoRepository.findById(id);
        if (postoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Posto de trabalho não encontrado.");
        }

        PostoTrabalho posto = postoOptional.get();
        posto.setNome(dto.nome());
        posto.setDescricao(dto.descricao());

        postoRepository.save(posto);
        return ResponseEntity.ok("Posto de trabalho atualizado com sucesso.");
    }

    @Operation(summary = "Deletar um posto de trabalho")
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deletarPosto(@PathVariable Long id) {
        if (!postoRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Posto de trabalho não encontrado.");
        }

        postoRepository.deleteById(id);
        return ResponseEntity.ok("Posto de trabalho deletado com sucesso.");
    }
}