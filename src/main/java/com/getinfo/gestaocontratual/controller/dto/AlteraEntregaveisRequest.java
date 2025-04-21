package com.getinfo.gestaocontratual.controller.dto;

import java.sql.Date;

public record AlteraEntregaveisRequest(String nome, Date dtInicio, Date dtFim, boolean Status) {
}
