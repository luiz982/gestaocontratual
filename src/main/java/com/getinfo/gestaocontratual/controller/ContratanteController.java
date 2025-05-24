package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.CreateContratanteRequest;
import com.getinfo.gestaocontratual.entities.Contratante;
import com.getinfo.gestaocontratual.service.ContratanteService;
import com.getinfo.gestaocontratual.controller.dto.ContratanteResumoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import com.getinfo.gestaocontratual.utils.Validadores;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
@Tag(name = "Contratantes", description = "Gerenciamento de empresas publicas e privadas")
@RestController
@RequestMapping("/contratantes")
public class ContratanteController {

    private final ContratanteService contratanteService;

    @Autowired
    public ContratanteController(ContratanteService contratanteService) {
        this.contratanteService = contratanteService;
    }

    @Operation(summary = "Listar todos os contratantes", description = "Retorna uma lista com todos os contratantes cadastrados.")
    @GetMapping
    public ResponseEntity<List<Contratante>> listarTodos() {
        return ResponseEntity.ok(contratanteService.listarTodos());
    }

    @Operation(summary = "Buscar contratante por ID", description = "Retorna os dados do contratante correspondente ao ID informado.")
    @GetMapping("/{id}")
    public ResponseEntity<Contratante> buscarPorId(@PathVariable Long id) {
        return contratanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar contratante por CNPJ", description = "Retorna os dados do contratante com o CNPJ informado.")
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Contratante> buscarPorCnpj(@PathVariable String cnpj) {
        return contratanteService.buscarPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo contratante", description = "Cria um novo contratante com os dados fornecidos no corpo da requisição.")
    @PostMapping
    public ResponseEntity<?> salvar(@Valid @RequestBody CreateContratanteRequest request) {
        if (!Validadores.isCnpjValido(request.cnpj())) {
            return ResponseEntity.badRequest().body("CNPJ inválido.");
        }

        if (request.cep() != null && !Validadores.ValidarCEP(request.cep())) {
            return ResponseEntity.badRequest().body("CEP inválido.");
        }

        if (request.responsavelLegalCpf() != null && !Validadores.isCpfValido(request.responsavelLegalCpf())) {
            return ResponseEntity.badRequest().body("CPF do responsável legal inválido.");
        }

        if (request.emailCorporativo() != null && !Validadores.isEmailValido(request.emailCorporativo())) {
            return ResponseEntity.badRequest().body("E-mail corporativo inválido.");
        }

        if (request.responsavelLegalEmail() != null && !Validadores.isEmailValido(request.responsavelLegalEmail())) {
            return ResponseEntity.badRequest().body("E-mail do responsável legal inválido.");
        }

        Contratante contratante = new Contratante();
        contratante.setCnpj(request.cnpj());
        contratante.setRazaoSocial(request.razaoSocial());
        contratante.setNomeFantasia(request.nomeFantasia());
        contratante.setInscricaoEstadual(request.inscricaoEstadual());
        contratante.setInscricaoMunicipal(request.inscricaoMunicipal());
        contratante.setEmailCorporativo(request.emailCorporativo());
        contratante.setSite(request.site());
        contratante.setDataFundacao(request.dataFundacao());
        contratante.setTelefone(request.telefone());
        contratante.setTelefoneFixo(request.telefoneFixo());
        contratante.setCep(request.cep());
        contratante.setBairro(request.bairro());
        contratante.setRua(request.rua());
        contratante.setCidade(request.cidade());
        contratante.setEstado(request.estado());
        contratante.setTipoEmpresa(request.tipoEmpresa());
        contratante.setBanco(request.banco());
        contratante.setAgencia(request.agencia());

        contratante.setResponsavelLegalCpf(request.responsavelLegalCpf());
        contratante.setResponsavelLegalNome(request.responsavelLegalNome());
        contratante.setResponsavelLegalTelefone(request.responsavelLegalTelefone());
        contratante.setResponsavelLegalEmail(request.responsavelLegalEmail());

        Contratante saved = contratanteService.salvar(contratante);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Deletar contratante por ID", description = "Remove o contratante correspondente ao ID informado.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contratanteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar contratante por ID", description = "Atualiza os dados de um contratante existente com base no ID informado.")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody CreateContratanteRequest request) {
        if (!Validadores.isCnpjValido(request.cnpj())) {
            return ResponseEntity.badRequest().body("CNPJ inválido.");
        }

        if (request.cep() != null && !Validadores.ValidarCEP(request.cep())) {
            return ResponseEntity.badRequest().body("CEP inválido.");
        }

        if (request.responsavelLegalCpf() != null && !Validadores.isCpfValido(request.responsavelLegalCpf())) {
            return ResponseEntity.badRequest().body("CPF do responsável legal inválido.");
        }

        return contratanteService.buscarPorId(id)
                .map(contratante -> {
                    contratante.setCnpj(request.cnpj());
                    contratante.setRazaoSocial(request.razaoSocial());
                    contratante.setNomeFantasia(request.nomeFantasia());
                    contratante.setInscricaoEstadual(request.inscricaoEstadual());
                    contratante.setInscricaoMunicipal(request.inscricaoMunicipal());
                    contratante.setEmailCorporativo(request.emailCorporativo());
                    contratante.setSite(request.site());
                    contratante.setDataFundacao(request.dataFundacao());
                    contratante.setTelefone(request.telefone());
                    contratante.setTelefoneFixo(request.telefoneFixo());
                    contratante.setCep(request.cep());
                    contratante.setBairro(request.bairro());
                    contratante.setRua(request.rua());
                    contratante.setCidade(request.cidade());
                    contratante.setEstado(request.estado());
                    contratante.setTipoEmpresa(request.tipoEmpresa());
                    contratante.setBanco(request.banco());
                    contratante.setAgencia(request.agencia());

                    contratante.setResponsavelLegalCpf(request.responsavelLegalCpf());
                    contratante.setResponsavelLegalNome(request.responsavelLegalNome());
                    contratante.setResponsavelLegalTelefone(request.responsavelLegalTelefone());
                    contratante.setResponsavelLegalEmail(request.responsavelLegalEmail());

                    Contratante atualizado = contratanteService.salvar(contratante);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar contratantes com número de contratos ativos",
            description = "Retorna uma lista de contratantes com nome, CNPJ e quantidade de contratos ativos.")
    @GetMapping("/resumo")
    public ResponseEntity<List<ContratanteResumoDTO>> listarResumoComContratosAtivos() {
        List<ContratanteResumoDTO> resumo = contratanteService.listarResumoComContratosAtivos();
        return ResponseEntity.ok(resumo);
    }
}