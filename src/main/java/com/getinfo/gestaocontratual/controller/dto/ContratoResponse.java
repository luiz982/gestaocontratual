package com.getinfo.gestaocontratual.controller.dto;

import com.getinfo.gestaocontratual.entities.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ContratoResponse {
    private Long idContrato;
    private Long numContrato;
    private Date dtInicio;
    private Date dtFim;
    private Date dtAlteracao;
    private Integer idContratante;
    private Long status;
    private String tipoContrato;
    private List<EntregaveisResponse> entregaveis;
    private List<PostoTrabalhoResponse> postosTrabalho;
    private List<DocumentoResponse> documentos;
    private Contratante contratante;
    private List<ColaboradorResponse> colaborador;

    public List<ColaboradorResponse> getColaborador() {
        return colaborador;
    }

    public void setColaborador(List<ColaboradorResponse> colaborador) {
        this.colaborador = colaborador;
    }

    public Contratante getContratante() {
        return contratante;
    }

    public void setContratante(Contratante contratante) {
        this.contratante = contratante;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public Long getNumContrato() {
        return numContrato;
    }

    public void setNumContrato(Long numContrato) {
        this.numContrato = numContrato;
    }

    public Date getDtInicio() {
        return dtInicio;
    }

    public void setDtInicio(Date dtInicio) {
        this.dtInicio = dtInicio;
    }

    public Date getDtFim() {
        return dtFim;
    }

    public void setDtFim(Date dtFim) {
        this.dtFim = dtFim;
    }

    public Date getDtAlteracao() {
        return dtAlteracao;
    }

    public void setDtAlteracao(Date dtAlteracao) {
        this.dtAlteracao = dtAlteracao;
    }

    public Integer getIdContratante() {
        return idContratante;
    }

    public void setIdContratante(Integer idContratante) {
        this.idContratante = idContratante;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public List<EntregaveisResponse> getEntregaveis() {
        return entregaveis;
    }

    public void setEntregaveis(List<EntregaveisResponse> entregaveis) {
        this.entregaveis = entregaveis;
    }

    public List<PostoTrabalhoResponse> getPostosTrabalho() {
        return postosTrabalho;
    }

    public void setPostosTrabalho(List<PostoTrabalhoResponse> postosTrabalho) {
        this.postosTrabalho = postosTrabalho;
    }

    public List<DocumentoResponse> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoResponse> documentos) {
        this.documentos = documentos;
    }
}