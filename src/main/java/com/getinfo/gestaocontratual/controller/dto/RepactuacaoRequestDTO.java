package com.getinfo.gestaocontratual.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class RepactuacaoRequestDTO {

    @NotNull(message = "ID do contrato é obrigatório")
    private Long idContrato;

    @NotNull(message = "Data fim do contrato é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dtFimContrato;

    @NotBlank(message = "Nome da repactuação é obrigatório")
    private String nome;

    private String descricao;

    // Construtores
    public RepactuacaoRequestDTO() {}

    public RepactuacaoRequestDTO(Long idContrato, Date dtFimContrato, String nome, String descricao) {
        this.idContrato = idContrato;
        this.dtFimContrato = dtFimContrato;
        this.nome = nome;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public Date getDtFimContrato() {
        return dtFimContrato;
    }

    public void setDtFimContrato(Date dtFimContrato) {
        this.dtFimContrato = dtFimContrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}