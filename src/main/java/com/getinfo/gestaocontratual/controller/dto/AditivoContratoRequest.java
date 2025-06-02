package com.getinfo.gestaocontratual.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record AditivoContratoRequest(

        Long idContrato,

        String documentoBase64,

        String descricao,

        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dataVigencia,

        String justificativa,

        String tipoAditivo
) { }

