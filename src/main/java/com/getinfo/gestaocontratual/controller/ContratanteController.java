package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.entities.Contratante;
import com.getinfo.gestaocontratual.services.ContratanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contratantes")
public class ContratanteController {

    private final ContratanteService contratanteService;

    @Autowired
    public ContratanteController(ContratanteService contratanteService) {
        this.contratanteService = contratanteService;
    }

    @GetMapping
    public ResponseEntity<List<Contratante>> listarTodos() {
        return ResponseEntity.ok(contratanteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contratante> buscarPorId(@PathVariable Long id) {
        return contratanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Contratante> buscarPorCnpj(@PathVariable String cnpj) {
        return contratanteService.buscarPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Contratante> salvar(@RequestBody Contratante contratante) {
        return ResponseEntity.ok(contratanteService.salvar(contratante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contratanteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
