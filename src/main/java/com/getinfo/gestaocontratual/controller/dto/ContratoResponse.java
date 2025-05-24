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
    private String status;
    private String tipoServico;
    private String responsavel;
    private List<EntregaveisResponse> entregaveis;
    private List<DocumentoResponse> documentos;
    private Contratante contratante;
    private List<ColaboradorResponse> colaborador;

    public ContratoResponse(Long idContrato, Long numContrato, Date dtInicio, Date dtFim, Date dtAlteracao, Integer idContratante, String status, String tipoServico, String responsavel, List<EntregaveisResponse> entregaveis, List<DocumentoResponse> documentos, Contratante contratante, List<ColaboradorResponse> colaborador) {
        this.idContrato = idContrato;
        this.numContrato = numContrato;
        this.dtInicio = dtInicio;
        this.dtFim = dtFim;
        this.dtAlteracao = dtAlteracao;
        this.idContratante = idContratante;
        this.status = status;
        this.tipoServico = tipoServico;
        this.responsavel = responsavel;
        this.entregaveis = entregaveis;
        this.documentos = documentos;
        this.contratante = contratante;
        this.colaborador = colaborador;
    }

    public List<ColaboradorResponse> getColaborador() {
        return colaborador;
    }

    public void setColaborador(List<ColaboradorResponse> colaborador) {
        this.colaborador = colaborador;
    }

    public Contratante getContratante() {
        return contratante;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public List<EntregaveisResponse> getEntregaveis() {
        return entregaveis;
    }

    public void setEntregaveis(List<EntregaveisResponse> entregaveis) {
        this.entregaveis = entregaveis;
    }

    public List<DocumentoResponse> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoResponse> documentos) {
        this.documentos = documentos;
    }
}