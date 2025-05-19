package com.getinfo.gestaocontratual.controller.dto;

import com.getinfo.gestaocontratual.entities.StatusEntregavel;

import java.sql.Date;
import java.util.List;

public record CreateEntregaveisContratoRequest(
                                               String nome,
                                               Date dtInicio,
                                               Date dtFim,
                                               StatusEntregavel Status,
                                               String descricao,
                                               List<EntregavelColaboradorRequest> colaboradores) {

}
