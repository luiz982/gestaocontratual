package com.getinfo.gestaocontratual.controller.dto;


public record CreatePostoTrabalhoRequest(
        Long idContrato,
        String nome,
        String descricao
) {}
