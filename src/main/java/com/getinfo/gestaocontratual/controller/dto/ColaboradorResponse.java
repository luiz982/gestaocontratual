package com.getinfo.gestaocontratual.controller.dto;

public record ColaboradorResponse(Long id, String cpf, String nome, String cargo, boolean situacao, String funcaocontrato) {
}
