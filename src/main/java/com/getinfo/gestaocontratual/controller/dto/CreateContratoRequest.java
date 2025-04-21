package com.getinfo.gestaocontratual.controller.dto;
import java.util.Date;
import java.util.List;

public record CreateContratoRequest(Long numContrato, Date dtInicio, Date dtFim, Date dtAlteracao, Long idStatus , String tipoContrato ,
                                    Long idContratante,
                                    List<AlteraPostosTrabalhoRequest> postos,
                                    List<AlteraDocumentoRequest> documentos,
                                    List<AlteraEntregaveisRequest> entregaveis) {
}
