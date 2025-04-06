package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.CreateDocumentoRequest;
import com.getinfo.gestaocontratual.controller.dto.DocumentoResponse;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.Documentos;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import com.getinfo.gestaocontratual.repository.DocumentoRepository;
import com.getinfo.gestaocontratual.utils.Validadores;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    @Autowired
    private DocumentoRepository documentoRepository;
    @Autowired
    private ContratoRepository contratoRepository;

    @Operation(summary = "Cadastro de documentos")
    @Transactional
    @PostMapping("/criarDocumento")
    public ResponseEntity<?> criarDocumento(@RequestBody CreateDocumentoRequest request) {
        int MAX_FILE_SIZE = 5 * 1024 * 1024;
        int MAX_BASE64_LENGTH = (int) Math.ceil(MAX_FILE_SIZE * 1.37);

        byte[] conteudoDecodificado;


        if (request.conteudoBase64() == null || request.conteudoBase64().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O campo de conteúdo do documento está vazio ou nulo.");
        }

        if (request.conteudoBase64().length() > MAX_BASE64_LENGTH) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O documento excede o limite de 5MB.");
        }

        if (!Validadores.isBase64Valido(request.conteudoBase64())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Tipo de documento não permitido.");
        }

        try {
            conteudoDecodificado = Base64.getDecoder().decode(request.conteudoBase64());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O documento enviado não é um Base64 válido.");
        }

        Optional<Contrato> contratoOptional = contratoRepository.findById(request.idContrato());

        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erro: Contrato não encontrado com o ID informado.");
        }

        Documentos documento = new Documentos();
        documento.setContrato(contratoOptional.orElse(null));
        documento.setNome(request.nome());
        documento.setConteudo(conteudoDecodificado);

        Documentos salvo = documentoRepository.save(documento);
        return ResponseEntity.ok(salvo);
    }
    @Operation(summary = "Deletar documento pelo id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarDocumento(@PathVariable Long id) {
        Optional<Documentos> doc = documentoRepository.findById(id);

        if (doc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erro: Documento não encontrado com o ID informado.");
        }

        documentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos os documentos de um contrato")
    @GetMapping("/{idContrato}")
    public ResponseEntity<List<DocumentoResponse>> listarDocumentosPorContrato(@PathVariable Long idContrato) {
        List<DocumentoResponse> response = documentoRepository.findByContratoId(idContrato).stream()
                .map(doc -> new DocumentoResponse(
                        doc.getNome(),
                        Base64.getEncoder().encodeToString(doc.getConteudo())
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}