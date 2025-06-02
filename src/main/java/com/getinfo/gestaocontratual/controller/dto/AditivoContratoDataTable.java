package com.getinfo.gestaocontratual.controller.dto;

public record AditivoContratoDataTable(
        Long idAditivo,
        Long idContrato,
        String descricao,
        String dataVigencia,
        String justificativa,
        String tipoAditivo,
        String nomeFantasia,
        String razaoSocial,
        Long numContrato
) {}