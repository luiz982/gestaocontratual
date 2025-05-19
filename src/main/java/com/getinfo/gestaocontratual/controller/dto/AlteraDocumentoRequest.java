package com.getinfo.gestaocontratual.controller.dto;

public record AlteraDocumentoRequest(
        String nome,
        String conteudoBase64
) {
}
