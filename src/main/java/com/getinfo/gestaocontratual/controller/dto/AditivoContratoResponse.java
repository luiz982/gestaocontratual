package com.getinfo.gestaocontratual.controller.dto;

public record AditivoContratoResponse(
        Long idAditivo,
        Long idContrato,
        String descricao,
        String dataVigencia,
        String justificativa,
        String tipoAditivo
) {}
