package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.DocumentoResponse;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.Documentos;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import com.getinfo.gestaocontratual.repository.DocumentoRepository;
import com.getinfo.gestaocontratual.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.getinfo.gestaocontratual.controller.dto.AlteraDocumentoRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Documentos", description = "Gerenciamento de documentos")
@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    @Autowired
    private DocumentoRepository documentoRepository;
    @Autowired
    private ContratoRepository contratoRepository;
    @Autowired
    private DocumentoService documentoService;

    @Operation(summary = "Cadastro de documentos")
    @Transactional
    @PostMapping("/criarDocumento")
    public ResponseEntity<?> criarDocumento(@RequestPart("documento") MultipartFile file, @RequestParam("nome") String nome, @RequestParam("idContrato") Long idContrato) {
        Optional<Contrato> contratoOptional = contratoRepository.findById(idContrato);

        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erro: Contrato não encontrado com o ID informado.");
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            documentoService.uploadFile(fileName, file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao fazer upload do arquivo para o Supabase Storage: " + e.getMessage());
        }

        String fileUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co/storage/v1/object/public/" + "documentos/" + fileName;

        Documentos documento = new Documentos();
        documento.setContrato(contratoOptional.orElse(null));
        documento.setNome(nome);
        documento.setUrl(fileUrl);
        Documentos salvo = documentoRepository.save(documento);
        return ResponseEntity.ok(salvo);
    }

    @Operation(summary = "Alterar documento")
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> alterarDocumento(@PathVariable Long id, @RequestPart(value = "documento", required = false) MultipartFile file, @RequestPart("nome") String nome) {
        Optional<Documentos> documentoOptional = documentoRepository.findById(id);

        if (documentoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erro: Documento não encontrado com o ID informado.");
        }

        Documentos documento = documentoOptional.get();
        String fileUrl = documento.getUrl();

        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            try {
                documentoService.uploadFile(fileName, file);

                documentoService.deleteFile(fileUrl.substring(fileUrl.lastIndexOf('/') + 1));

                fileUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co/storage/v1/object/public/" + "documentos/" + fileName;
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao fazer upload do arquivo para o Supabase Storage: " + e.getMessage());
            }
        }

        documento.setNome(nome);
        documento.setUrl(fileUrl);
        Documentos salvo = documentoRepository.save(documento);
        return ResponseEntity.ok(salvo);
    }

    @Operation(summary = "Deletar documento pelo id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarDocumento(@PathVariable Long id) {
        Optional<Documentos> docOptional = documentoRepository.findById(id);

        if (docOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erro: Documento não encontrado com o ID informado.");
        }
        Documentos doc = docOptional.get();
        documentoService.deleteFile(doc.getUrl().substring(doc.getUrl().lastIndexOf('/') + 1));
        documentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos os documentos de um contrato")
    @GetMapping("/{idContrato}")
    public ResponseEntity<List<DocumentoResponse>> listarDocumentosPorContrato(@PathVariable Long idContrato) {
        List<Documentos> documentos = documentoRepository.findByContratoId(idContrato);

        List<DocumentoResponse> response = documentos.stream()
                .map(doc -> new DocumentoResponse(doc.getIdDocumento(), doc.getNome(), doc.getUrl()))
                .toList();

        return ResponseEntity.ok(response);
    }
}

