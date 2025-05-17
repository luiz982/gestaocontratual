package com.getinfo.gestaocontratual.controller.dto;
import java.sql.Date;
import java.util.List;

public record CreateEntregaveisRequest(Long idContrato, 
                                String nome, 
                                Date dtInicio, 
                                Date dtFim, 
                                boolean Status,
                                List<EntregavelColaboradorRequest> colaboradores) {
    
}
