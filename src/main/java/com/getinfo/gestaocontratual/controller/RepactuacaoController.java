package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.RepactuacaoRequestDTO;
import com.getinfo.gestaocontratual.controller.dto.RepactuacaoResponseDTO;
import com.getinfo.gestaocontratual.service.RepactuacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repactuacoes")
public class RepactuacaoController {

    @Autowired
    private RepactuacaoService repactuacaoService;

    @PostMapping
    public ResponseEntity<RepactuacaoResponseDTO> criarRepactuacao(@Valid @RequestBody RepactuacaoRequestDTO requestDTO) {
        RepactuacaoResponseDTO responseDTO = repactuacaoService.criarRepactuacao(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RepactuacaoResponseDTO>> listarTodasRepactuacoes() {
        List<RepactuacaoResponseDTO> repactuacoes = repactuacaoService.listarTodasRepactuacoes();
        return ResponseEntity.ok(repactuacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepactuacaoResponseDTO> buscarRepactuacaoPorId(@PathVariable Long id) {
        RepactuacaoResponseDTO responseDTO = repactuacaoService.buscarRepactuacaoPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/contrato/{idContrato}")
    public ResponseEntity<List<RepactuacaoResponseDTO>> listarRepactuacoesPorContrato(@PathVariable Long idContrato) {
        List<RepactuacaoResponseDTO> repactuacoes = repactuacaoService.listarRepactuacoesPorContrato(idContrato);
        return ResponseEntity.ok(repactuacoes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepactuacaoResponseDTO> atualizarRepactuacao(
            @PathVariable Long id,
            @Valid @RequestBody RepactuacaoRequestDTO requestDTO) {
        RepactuacaoResponseDTO responseDTO = repactuacaoService.atualizarRepactuacao(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRepactuacao(@PathVariable Long id) {
        repactuacaoService.deletarRepactuacao(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/aplicar")
    public ResponseEntity<String> aplicarRepactuacao(@PathVariable Long id) {
        repactuacaoService.aplicarRepactuacao(id);
        return ResponseEntity.ok("Repactuação aplicada com sucesso! Data fim do contrato foi atualizada.");
    }
}