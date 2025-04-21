package com.getinfo.gestaocontratual.controller.dto;

public record PostoTrabalhoResponse(
        Long id,
        Long idContrato,
        String nome,
        String descricao
) {}
