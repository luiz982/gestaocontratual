package com.getinfo.gestaocontratual.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.getinfo.gestaocontratual.entities.TipoContrato;

import java.util.Date;
import java.util.List;

public record AlteraContratoRequest(
        Long numContrato,
        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dtInicio,
        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dtFim,
        String status,
        String tipoServico,
        Long idContratante,
        String responsavel,
        List<ContratoColaboradorRequest> colaboradores,
        List<CreateEntregaveisContratoRequest> entregaveis
) {}