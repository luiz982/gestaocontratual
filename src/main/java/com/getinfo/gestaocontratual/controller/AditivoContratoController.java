package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.entities.AditivoContrato;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.repository.AditivoContratoRepository;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import com.getinfo.gestaocontratual.controller.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.ZoneId;

@Tag(name = "Aditivos de Contrato", description = "Gerenciamento de aditivos de contratos")
@RestController
@RequestMapping("/aditivos")
@RequiredArgsConstructor
public class AditivoContratoController {

    @Autowired
    private AditivoContratoRepository aditivoContratoRepository;
    @Autowired
    private ContratoRepository contratoRepository;


    @Operation(summary = "Lista todos os aditivos")
    @GetMapping
    public ResponseEntity<List<AditivoContratoResponse>> listarAditivos() {
        List<AditivoContrato> aditivos = aditivoContratoRepository.findAll();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<AditivoContratoResponse> responseList = aditivos.stream().map(aditivo -> {
            LocalDate localDate = aditivo.getDataVigencia().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            return new AditivoContratoResponse(
                    aditivo.getIdAditivo(),
                    aditivo.getContrato().getIdContrato(),
                    aditivo.getDescricao(),
                    formatter.format(localDate),
                    aditivo.getJustificativa(),
                    aditivo.getTipoAditivo()
            );
        }).toList();

        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Busca um aditivo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<AditivoContrato> aditivoOptional = aditivoContratoRepository.findById(id);
        if (aditivoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Aditivo não encontrado.");
        }

        AditivoContrato aditivo = aditivoOptional.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate localDate = aditivo.getDataVigencia().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        AditivoContratoResponse response = new AditivoContratoResponse(
                aditivo.getIdAditivo(),
                aditivo.getContrato().getIdContrato(),
                aditivo.getDescricao(),
                formatter.format(localDate),
                aditivo.getJustificativa(),
                aditivo.getTipoAditivo()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cria um novo aditivo")
    @PostMapping
    @Transactional
    public ResponseEntity<?> criarAditivo(@RequestBody AditivoContratoRequest request) {
        Optional<Contrato> contratoOptional = contratoRepository.findById(request.idContrato());
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Contrato não encontrado.");
        }

        if (request.dataVigencia() == null) {
            return ResponseEntity.badRequest().body("Data de vigência é obrigatória.");
        }

        if (request.justificativa() == null || request.justificativa().isBlank()) {
            return ResponseEntity.badRequest().body("Justificativa é obrigatória.");
        }

        if (request.tipoAditivo() == null || request.tipoAditivo().isBlank()) {
            return ResponseEntity.badRequest().body("Tipo de aditivo é obrigatório.");
        }


        AditivoContrato aditivo = new AditivoContrato();
        aditivo.setContrato(contratoOptional.get());
        aditivo.setDescricao(request.descricao());
        aditivo.setDataVigencia(request.dataVigencia());
        aditivo.setJustificativa(request.justificativa());
        aditivo.setTipoAditivo(request.tipoAditivo());

        if (request.documentoBase64() != null && !request.documentoBase64().isBlank()) {
            try {
                byte[] documentoBytes = Base64.getDecoder().decode(request.documentoBase64());
                aditivo.setDocumento(documentoBytes);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Documento Base64 inválido.");
            }
        }

        try {
            AditivoContrato salvo = aditivoContratoRepository.save(aditivo);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Aditivo criado com sucesso. ID: " + salvo.getIdAditivo());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar o aditivo: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualiza um aditivo existente")
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> atualizarAditivo(@PathVariable Long id, @RequestBody AditivoContratoRequest request) {
        Optional<AditivoContrato> aditivoOptional = aditivoContratoRepository.findById(id);
        if (aditivoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Aditivo não encontrado.");
        }
        Optional<Contrato> contratoOptional = contratoRepository.findById(request.idContrato());
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Contrato não encontrado.");
        }

        if (request.dataVigencia() == null) {
            return ResponseEntity.badRequest().body("Data de vigência é obrigatória.");
        }

        if (request.justificativa() == null || request.justificativa().isBlank()) {
            return ResponseEntity.badRequest().body("Justificativa é obrigatória.");
        }

        if (request.tipoAditivo() == null || request.tipoAditivo().isBlank()) {
            return ResponseEntity.badRequest().body("Tipo de aditivo é obrigatório.");
        }

        AditivoContrato aditivo = aditivoOptional.get();
        aditivo.setContrato(contratoOptional.get());
        aditivo.setDescricao(request.descricao());
        aditivo.setDataVigencia(request.dataVigencia());
        aditivo.setJustificativa(request.justificativa());
        aditivo.setTipoAditivo(request.tipoAditivo());

        if (request.documentoBase64() != null && !request.documentoBase64().isBlank()) {
            try {
                byte[] documentoBytes = Base64.getDecoder().decode(request.documentoBase64());
                aditivo.setDocumento(documentoBytes);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Documento Base64 inválido.");
            }
        }

        try {
            AditivoContrato salvo = aditivoContratoRepository.save(aditivo);

            AditivoContratoResponse response = new AditivoContratoResponse(
                    salvo.getIdAditivo(),
                    salvo.getContrato().getIdContrato(),
                    salvo.getDescricao(),
                    salvo.getDataVigencia().toString(),
                    salvo.getJustificativa(),
                    salvo.getTipoAditivo()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o aditivo: " + e.getMessage());
        }
    }

    @Operation(summary = "Deleta um aditivo")
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deletarAditivo(@PathVariable Long id) {
        if (!aditivoContratoRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Aditivo não encontrado.");
        }
        aditivoContratoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista aditivos por ID do contrato")
    @GetMapping("/por-contrato/{idContrato}")
    public ResponseEntity<?> listarPorContrato(@PathVariable Long idContrato) {
        Optional<Contrato> contratoOptional = contratoRepository.findById(idContrato);
        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Contrato não encontrado.");
        }
        List<AditivoContrato> aditivos = aditivoContratoRepository.findByContrato_IdContrato(idContrato);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<AditivoContratoDataTable> responseList = aditivos.stream().map(aditivo -> {
            LocalDate localDate = aditivo.getDataVigencia().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            return new AditivoContratoDataTable(
                    aditivo.getIdAditivo(),
                    aditivo.getContrato().getIdContrato(),
                    aditivo.getDescricao(),
                    formatter.format(localDate),
                    aditivo.getJustificativa(),
                    aditivo.getTipoAditivo(),
                    aditivo.getContrato().getIdContratante().getNomeFantasia(),
                    aditivo.getContrato().getIdContratante().getRazaoSocial(),
                    aditivo.getContrato().getNumContrato()
            );
        }).toList();

        return ResponseEntity.ok(responseList);
    }
}
