package com.getinfo.gestaocontratual.controller.dto;
import java.sql.Date;
import java.util.List;

import com.getinfo.gestaocontratual.entities.StatusEntregavel;

public record CreateEntregaveisRequest(Long idContrato, 
                                String nome, 
                                Date dtInicio, 
                                Date dtFim, 
                                StatusEntregavel Status,
                                List<EntregavelColaboradorRequest> colaboradores) {
    
}
