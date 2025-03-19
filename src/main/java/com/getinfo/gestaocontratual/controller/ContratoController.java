package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.CreateContratoRequest;
import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.*;
import com.getinfo.gestaocontratual.utils.Validadores;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Base64;

@RestController
@RequestMapping("/contratos")
public class ContratoController {
    private final ContratoRepository contratoRepository;
    private final ContratanteRepository contratanteRepository;
    private final TipoContratoRepository tipoContratoRepository;
    private final StatusRepository statusRepository;

    public ContratoController(ContratoRepository contratoRepository, ContratanteRepository contratanteRepository, TipoContratoRepository tipoContratoRepository, StatusRepository statusRepository) {
        this.contratoRepository = contratoRepository;
        this.contratanteRepository = contratanteRepository;
        this.tipoContratoRepository = tipoContratoRepository;
        this.statusRepository = statusRepository;
    }
    @Operation(summary = "Cadastro de contrato")
    @Transactional
    @PostMapping("/criarContrato")
    public ResponseEntity<String> criarContrato(@RequestBody CreateContratoRequest dto) {
        TipoContrato tipoContrato = tipoContratoRepository.findById(dto.idTipo()).orElse(null);
        if (tipoContrato == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Tipo de Contrato não encontrado!");
        }

        Contratante contratante = contratanteRepository.findById(dto.idContratante()).orElse(null);
        if (contratante == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Contratante não encontrado!");
        }

        int MAX_FILE_SIZE = 5 * 1024 * 1024;
        int MAX_BASE64_LENGTH = (int) Math.ceil(MAX_FILE_SIZE * 1.37);
        byte[] documentoBytes = null;

        if (dto.documento() != null && !dto.documento().isBlank()) {
            if (dto.documento().length() > MAX_BASE64_LENGTH) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erro: O documento excede o limite de 5MB.");
            }
            try {
                documentoBytes = Base64.getDecoder().decode(dto.documento());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erro: O documento enviado não é um Base64 válido.");
            }
        }

        if (!Validadores.isBase64Valido(dto.documento())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Tipo de documento não permitido.");
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
        contrato.setIdTipo(tipoContrato);
        contrato.setDocumento(documentoBytes);

        contratoRepository.save(contrato);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Contrato registrado com sucesso! Id do contrato: " + contrato.getIdContrato());
    }

    @Operation(summary = "Retorna todos os contratos cadastrados")
    @GetMapping("/contratos")
    public ResponseEntity<List<Contrato>> contratos(){
        var contratos = contratoRepository.findAll();

        return ResponseEntity.ok(contratos);

    }

    @Operation(summary = "Retorna um contrato pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarContratoPorId(@PathVariable Long id) {
        Optional<Contrato> contrato = contratoRepository.findById(id);
        if (contrato.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Contrato não encontrado!");
        }
        return ResponseEntity.ok(contrato);
    }

    @Operation(summary = "Atualiza um contrato")
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarContrato(@PathVariable Long id, @RequestBody CreateContratoRequest dto) {
        Optional<Contrato> contratoOptional = contratoRepository.findById(id);
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }

        Contrato contrato = contratoOptional.get();
        TipoContrato tipoContrato = tipoContratoRepository.findById(dto.idTipo()).orElse(null);
        if (tipoContrato == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Tipo de Contrato não encontrado!");
        }

        Contratante contratante = contratanteRepository.findById(dto.idContratante()).orElse(null);
        if (contratante == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Contratante não encontrado!");
        }

        int MAX_FILE_SIZE = 5 * 1024 * 1024;
        int MAX_BASE64_LENGTH = (int) Math.ceil(MAX_FILE_SIZE * 1.37);
        byte[] documentoBytes = null;

        if (dto.documento() != null && !dto.documento().isBlank()) {
            if (dto.documento().length() > MAX_BASE64_LENGTH) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erro: O documento excede o limite de 5MB.");
            }
            try {
                documentoBytes = Base64.getDecoder().decode(dto.documento());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erro: O documento enviado não é um Base64 válido.");
            }
        }

        if (!Validadores.isBase64Valido(dto.documento())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Tipo de documento não permitido.");
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
        contrato.setIdTipo(tipoContrato);
        contrato.setDocumento(documentoBytes);

        contratoRepository.save(contrato);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Contrato atualizado com sucesso! Id do contrato: " + contrato.getIdContrato());
    }

    @Operation(summary = "Deleta um contrato")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarContrato(@PathVariable Long id) {
        if (!contratoRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }
        contratoRepository.deleteById(id);
        return ResponseEntity.ok("Contrato deletado com sucesso");
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

}
