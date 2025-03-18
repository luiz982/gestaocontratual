package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.*;
import com.getinfo.gestaocontratual.utils.Validadores;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
public class EntregaveisController {

    private final EntregaveisRepository EntregaveisRepository;

    public EntregaveisController(EntregaveisRepository entregaveisRepository) {
        this.EntregaveisRepository = entregaveisRepository;
    }

    @GetMapping("/entregaveis")
    public ResponseEntity<List<Entregaveis>> Entregaveis(){
        var Entregaveis = EntregaveisRepository.findAll();

        return ResponseEntity.ok(Entregaveis);

    }
    
}
