package com.getinfo.gestaocontratual.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.getinfo.gestaocontratual.controller.dto.AlteraEntregaveisRequest;
import com.getinfo.gestaocontratual.controller.dto.AlteraPostosTrabalhoRequest;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public record CreateContratoRequest(
        Long numContrato,
        @JsonFormat(pattern = "yyyy-MM-dd") Date dtInicio,
        @JsonFormat(pattern = "yyyy-MM-dd") Date dtFim,
        @JsonFormat(pattern = "yyyy-MM-dd") Date dtAlteracao,
        Long idStatus,
        String tipoContrato,
        Long idContratante,
        List<AlteraPostosTrabalhoRequest> postos,
        List<AlteraEntregaveisRequest> entregaveis
) {}