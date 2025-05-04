package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.CreateStatusRequest;
import com.getinfo.gestaocontratual.entities.Status;
import com.getinfo.gestaocontratual.repository.StatusRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/status")
public class StatusController {
    private final StatusRepository statusRepository;

    public StatusController(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Operation(summary = "Cadastrar um novo status")
    @PostMapping("/criarStatus")
    public ResponseEntity<Status> criarStatus(@RequestBody CreateStatusRequest statusDto) {
        Status novoStatus = new Status();
        novoStatus.setNome(statusDto.nome());
        novoStatus.setDescricao(statusDto.descricao());
        statusRepository.save(novoStatus);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoStatus);
    }

    @Operation(summary = "Listar todos os status cadastrados")
    @GetMapping("/listarStatus")
    public ResponseEntity<List<Status>> listarStatus() {
        List<Status> statusList = statusRepository.findAll();
        return ResponseEntity.ok(statusList);
    }

    @Operation(summary = "Atualizar um status existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody CreateStatusRequest statusAtualizado) {
        Optional<Status> statusOptional = statusRepository.findById(id);
        if (statusOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Status não encontrado!");
        }
        Status status = statusOptional.get();
        status.setNome(statusAtualizado.nome());
        status.setDescricao(statusAtualizado.descricao());
        statusRepository.save(status);
        return ResponseEntity.ok("Status atualizado com sucesso!");
    }

    @Operation(summary = "Deletar um status pelo ID")
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deletarStatus(@PathVariable Long id) {
        if (!statusRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Status não encontrado!");
        }
        statusRepository.deleteById(id);
        return ResponseEntity.ok("Status deletado com sucesso!");
    }
}