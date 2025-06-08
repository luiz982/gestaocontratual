package com.getinfo.gestaocontratual.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class RepactuacaoResponseDTO {

    private Long idRepactuacao;
    private Long idContrato;
    private Long numeroContrato;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dtFimContrato;

    private String nome;
    private String descricao;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    // Construtores
    public RepactuacaoResponseDTO() {}

    public RepactuacaoResponseDTO(Long idRepactuacao, Long idContrato, Long numeroContrato,
                                  Date dtFimContrato, String nome, String descricao, Date createdAt) {
        this.idRepactuacao = idRepactuacao;
        this.idContrato = idContrato;
        this.numeroContrato = numeroContrato;
        this.dtFimContrato = dtFimContrato;
        this.nome = nome;
        this.descricao = descricao;
        this.createdAt = createdAt;
    }

    // Getters e Setters
    public Long getIdRepactuacao() {
        return idRepactuacao;
    }

    public void setIdRepactuacao(Long idRepactuacao) {
        this.idRepactuacao = idRepactuacao;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public Long getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(Long numeroContrato) {
        this.numeroContrato = numeroContrato;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}