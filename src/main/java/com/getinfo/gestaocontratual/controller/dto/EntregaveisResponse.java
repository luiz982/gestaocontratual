package com.getinfo.gestaocontratual.controller.dto;

import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.EntregaveisColaborador;
import com.getinfo.gestaocontratual.entities.StatusEntregavel;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

public record EntregaveisResponse(Long idEntregavel, String nome, Date dtInicio, Date dtFim, StatusEntregavel status, List<ColaboradorResponse> colaborador) {
    
}

