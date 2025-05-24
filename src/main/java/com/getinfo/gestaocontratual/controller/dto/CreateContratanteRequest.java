package com.getinfo.gestaocontratual.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
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

        @Min(value = 0, message = "Tipo de empresa deve ser 0 (Privada) ou 1 (Pública)")
        @Max(value = 1, message = "Tipo de empresa deve ser 0 (Privada) ou 1 (Pública)")
        Integer tipoEmpresa,

        String banco,

        String agencia,

        String responsavelLegalCpf,

        String responsavelLegalNome,

        String responsavelLegalTelefone,

        String responsavelLegalEmail
) {}