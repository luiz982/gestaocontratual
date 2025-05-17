package com.getinfo.gestaocontratual.controller.dto;

public record CreateDocumentoRequest(
        Long idContrato,
        String nome,
        String conteudoBase64
) {
}