package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.CreateContratoRequest;
import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.*;
import com.getinfo.gestaocontratual.utils.Validadores;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.Base64;

@RestController
public class ContratoController {
    private final ContratoRepository contratoRepository;
    private final ContratanteRepository contratanteRepository;
    private final TipoContratoRepository tipoContratoRepository;

    public ContratoController(ContratoRepository contratoRepository, ContratanteRepository contratanteRepository, TipoContratoRepository tipoContratoRepository) {
        this.contratoRepository = contratoRepository;
        this.contratanteRepository = contratanteRepository;
        this.tipoContratoRepository = tipoContratoRepository;
    }

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
            documentoBytes = Base64.getDecoder().decode(dto.documento());
        }

        if (dto.dtInicio() == null || dto.dtInicio().toString().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: A data de início não pode ser vazia.");
        }

        if (dto.cnpj() == null || dto.cnpj().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O CNPJ não pode ser vazio.");
        }

        if (!Validadores.isCnpjValido(dto.cnpj())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: CNPJ inválido ou não existe!");
        }

        Contrato contrato = new Contrato();
        contrato.setCnpj(dto.cnpj());
        contrato.setNumContrato(dto.numContrato());
        contrato.setDtFim(dto.dtFim());
        contrato.setDtInicio(dto.dtInicio());
        contrato.setDtAlteracao(dto.dtAlteracao());
        contrato.setIdContratante(contratante);
        contrato.setIdTipo(tipoContrato);
        contrato.setDocumento(documentoBytes);

        contratoRepository.save(contrato);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Contrato registrado com sucesso! Id do contrato: " + contrato.getIdContrato());
    }

}
