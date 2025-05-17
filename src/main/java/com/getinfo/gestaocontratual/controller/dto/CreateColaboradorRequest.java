package com.getinfo.gestaocontratual.controller.dto;

public record CreateColaboradorRequest(String cpf, String nome, String cargo, boolean situacao) {
}
