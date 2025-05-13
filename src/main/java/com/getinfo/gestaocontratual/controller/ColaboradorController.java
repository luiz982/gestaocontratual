package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.CreateColaboradorRequest;
import com.getinfo.gestaocontratual.controller.dto.CreateDocumentoRequest;
import com.getinfo.gestaocontratual.entities.Colaborador;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.repository.ColaboradorRepository;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Colaboradores", description = "Gerenciamento de colaboradores e funcionários da getinfo")
@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {

    private final ColaboradorRepository colaboradorRepository;
    private final ContratoRepository contratoRepository;

    public ColaboradorController(ColaboradorRepository colaboradorRepository, ContratoRepository contratoRepository) {
        this.colaboradorRepository = colaboradorRepository;
        this.contratoRepository = contratoRepository;
    }

    @Operation(summary = "Lista todos os colaboradores")
    @GetMapping
    public ResponseEntity<List<Colaborador>> listarTodos() {
        return ResponseEntity.ok(colaboradorRepository.findAll());
    }

    @Operation(summary = "Busca um colaborador por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return colaboradorRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Colaborador não encontrado."));
    }

    @Operation(summary = "Cria um novo colaborador")
    @PostMapping
    @Transactional
    public ResponseEntity<?> criar(@RequestBody CreateColaboradorRequest request) {
        if (colaboradorRepository.existsByCpf(request.cpf())) {
            return ResponseEntity
                    .status(404)
                    .body("Já existe um colaborador com esse CPF.");
        }

        Colaborador novo = new Colaborador();
        novo.setNome(request.nome());
        novo.setCpf(request.cpf());
        novo.setCargo(request.cargo());
        novo.setSituacao(request.situacao());

        return ResponseEntity.ok(colaboradorRepository.save(novo));
    }

    @Operation(summary = "Atualiza um colaborador existente")
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody CreateColaboradorRequest request) {
        Optional<Colaborador> optional = colaboradorRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(404).body("Colaborador não encontrado.");
        }

        Colaborador colaborador = optional.get();
        colaborador.setNome(request.nome());
        colaborador.setCpf(request.cpf());
        colaborador.setCargo(request.cargo());
        colaborador.setSituacao(request.situacao());

        return ResponseEntity.ok(colaboradorRepository.save(colaborador));
    }

    @Operation(summary = "Deleta um colaborador")
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!colaboradorRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Colaborador não encontrado.");
        }
        colaboradorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista colaboradores por ID do contrato")
    @GetMapping("/por-contrato/{idContrato}")
    public ResponseEntity<?> listarPorContrato(@PathVariable Long idContrato) {
        Optional<Contrato> contratoOptional = contratoRepository.findById(idContrato);
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Contrato não encontrado.");
        }

        List<Colaborador> colaboradores = contratoOptional.get().getColaboradores();
        return ResponseEntity.ok(colaboradores);
    }
}
