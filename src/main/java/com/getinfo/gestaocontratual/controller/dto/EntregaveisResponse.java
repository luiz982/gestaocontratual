package com.getinfo.gestaocontratual.controller.dto;

import com.getinfo.gestaocontratual.entities.Contrato;
import jakarta.persistence.*;

import java.sql.Date;

public record EntregaveisResponse(int idEntregavel, String nome, Date dtInicio, Date dtFim, boolean status) {
}

