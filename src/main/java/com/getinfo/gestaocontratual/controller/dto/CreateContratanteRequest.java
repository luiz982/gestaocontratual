package com.getinfo.gestaocontratual.controller.dto;

import java.time.LocalDate;

public record CreateContratanteRequest(
        String cnpj,
        String razaoSocial,
        String nomeFantasia,
        String inscricaoEstadual,
        String inscricaoMunicipal,
        String emailCorporativo,
        String site,
        LocalDate dataFundacao,
        String telefone,
        String cep,
        String bairro,
        String numeroDaCasa,
        String rua,
        String estado,
        String banco,
        String agencia
) {
}