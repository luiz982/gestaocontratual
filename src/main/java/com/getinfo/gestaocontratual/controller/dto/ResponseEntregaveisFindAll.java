package com.getinfo.gestaocontratual.controller.dto;

import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.StatusEntregavel;

import java.sql.Date;
import java.util.List;

public record ResponseEntregaveisFindAll(Long idEntregavel, Long idContrato, String nome, Date dtInicio, Date dtFim, StatusEntregavel status, String descricao , List<ColaboradorResponse> colaborador) {

}

