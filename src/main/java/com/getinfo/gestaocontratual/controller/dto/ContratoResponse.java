package com.getinfo.gestaocontratual.controller.dto;

import com.getinfo.gestaocontratual.entities.Documentos;
import com.getinfo.gestaocontratual.entities.Entregaveis;
import com.getinfo.gestaocontratual.entities.PostoTrabalho;
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
    private List<Entregaveis> entregaveis;
    private List<PostoTrabalho> postosTrabalho;
    private List<Documentos> documentos;

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

    public List<Entregaveis> getEntregaveis() {
        return entregaveis;
    }

    public void setEntregaveis(List<Entregaveis> entregaveis) {
        this.entregaveis = entregaveis;
    }

    public List<PostoTrabalho> getPostosTrabalho() {
        return postosTrabalho;
    }

    public void setPostosTrabalho(List<PostoTrabalho> postosTrabalho) {
        this.postosTrabalho = postosTrabalho;
    }

    public List<Documentos> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documentos> documentos) {
        this.documentos = documentos;
    }
}