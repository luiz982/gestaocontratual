package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.CreateEntregaveisRequest;
import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class EntregaveisController {

    private final EntregaveisRepository EntregaveisRepository;
    private final ContratoRepository contratoRepository;

    public EntregaveisController(EntregaveisRepository entregaveisRepository, ContratoRepository contratoRepository) {
        this.EntregaveisRepository = entregaveisRepository;
        this.contratoRepository = contratoRepository;
    }

    @GetMapping("/entregaveis")
    public ResponseEntity<List<Entregaveis>> Entregaveis(){
        var Entregaveis = EntregaveisRepository.findAll();

        return ResponseEntity.ok(Entregaveis);

    }

    @PostMapping("/criarEntregaveis")
    public ResponseEntity<String> CriarEntregaveis(@RequestBody CreateEntregaveisRequest dto) {

        if (dto.idContrato() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Numero do Contrato não informado!");
        }

        if (dto.nome() == null || dto.nome().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Nome do Entregavel não informado!");
        }

        if (dto.dtInicio() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Data de Inicio não informada!");
        }           
        
        if (dto.dtInicio().after(dto.dtFim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Data de Inicio maior que a Data de Fim!");
        }

        Optional<Contrato> contratoOptional = contratoRepository.findById(dto.idContrato());

        if (contratoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado");
        }

        Entregaveis entregaveis = new Entregaveis();

        entregaveis.setIdContrato(contratoOptional.orElse(null));
        entregaveis.setNome(dto.nome());
        entregaveis.setDtInicio(dto.dtInicio());
        entregaveis.setDtFim(dto.dtFim());
        entregaveis.setStatus(dto.Status());

        EntregaveisRepository.save(entregaveis);

        var msg = "Entregaveis criado com sucesso";
        return ResponseEntity.ok(msg);
    }

    @DeleteMapping("entregaveis/{id}")
    @Transactional
    public ResponseEntity<String> DeletarEntregaveis(@PathVariable("id") Long idEntregavel)
    {
        Entregaveis entregaveis = EntregaveisRepository.findById(idEntregavel)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entregaveis não encontrado"));

        EntregaveisRepository.delete(entregaveis);

        return ResponseEntity.ok("Entregaveis deletado com sucesso");
    }

    
        

    
}
