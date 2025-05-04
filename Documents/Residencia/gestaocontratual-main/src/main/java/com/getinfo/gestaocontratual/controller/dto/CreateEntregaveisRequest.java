package com.getinfo.gestaocontratual.controller.dto;
import java.sql.Date;

public record CreateEntregaveisRequest( Long idContrato, String nome, Date dtInicio, Date dtFim, boolean Status) {
    
}
