package com.getinfo.gestaocontratual.controller.dto;

import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.EntregaveisColaborador;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

public record EntregaveisResponse(int idEntregavel, String nome, Date dtInicio, Date dtFim, boolean status, List<ColaboradorResponse> colaborador) {
    
}

