package com.getinfo.gestaocontratual.controller.dto;

public record LoginResponse(String mensagem, String acessToken, long expiresIn) {

}
