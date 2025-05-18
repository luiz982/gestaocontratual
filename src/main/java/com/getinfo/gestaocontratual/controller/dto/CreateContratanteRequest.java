package com.getinfo.gestaocontratual.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

public record CreateContratanteRequest(
        @NotBlank(message = "CNPJ é obrigatório")
        String cnpj,

        @NotBlank(message = "Razão Social é obrigatória")
        String razaoSocial,

        @NotBlank(message = "Nome Fantasia é obrigatório")
        String nomeFantasia,

        String inscricaoEstadual,

        String inscricaoMunicipal,

        @Email(message = "E-mail corporativo inválido")
        String emailCorporativo,

        String site,

        LocalDate dataFundacao,

        String telefone,

        String telefoneFixo,

        String cep,

        String bairro,

        String rua,

        String cidade,

        String estado,

        String banco,

        String agencia,

        String responsavelLegalCpf,

        String responsavelLegalNome,

        String responsavelLegalTelefone,

        @Email(message = "E-mail do responsável legal inválido")
        String responsavelLegalEmail
) {}