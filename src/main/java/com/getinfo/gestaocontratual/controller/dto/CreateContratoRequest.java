package com.getinfo.gestaocontratual.controller.dto;
import java.util.Date;

public record CreateContratoRequest(Long numContrato, Date dtInicio, Date dtFim, Date dtAlteracao, String cnpj, String documento, Long idTipo, Long idContratante) {
}
