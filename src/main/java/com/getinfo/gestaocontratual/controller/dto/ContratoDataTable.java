package com.getinfo.gestaocontratual.controller.dto;

import com.getinfo.gestaocontratual.entities.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ContratoDataTable {
    private Long idContrato;
    private Long numContrato;
    private Date dtInicio;
    private Date dtFim;
    private Date dtAlteracao;
    private Integer idContratante;
    private String status;
    private TipoContrato tipoContrato;
    private String tipoServico;
    private String responsavel;
    private Contratante contratante;

    public ContratoDataTable(Long idContrato, Long numContrato, Date dtInicio, Date dtFim, Date dtAlteracao, Integer idContratante, String status, TipoContrato tipoContrato, String tipoServico, String responsavel, Contratante contratante) {
        this.idContrato = idContrato;
        this.numContrato = numContrato;
        this.dtInicio = dtInicio;
        this.dtFim = dtFim;
        this.dtAlteracao = dtAlteracao;
        this.idContratante = idContratante;
        this.status = status;
        this.tipoContrato = tipoContrato;
        this.tipoServico = tipoServico;
        this.responsavel = responsavel;
        this.contratante = contratante;
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

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

}