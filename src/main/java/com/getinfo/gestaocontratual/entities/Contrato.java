package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_contrato")
    private Long idContrato;

    @Column
    private Long numContrato;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dtInicio;

    @Column
    @Temporal(TemporalType.DATE)
    private Date dtFim;

    @Column
    @Temporal(TemporalType.DATE)
    private Date dtAlteracao;

    @Lob
    @Column
    private byte[] documento;

    @ManyToOne
    @JoinColumn(name = "id_tipo", referencedColumnName = "id_tipo", nullable = false)
    private TipoContrato idTipo;

    @ManyToOne
    @JoinColumn(name = "id_contratante", referencedColumnName = "id_contratante", nullable = false)
    private Contratante idContratante;

    @ManyToOne
    @JoinColumn(name = "id_status", referencedColumnName = "id_status")
    private Status status;

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


    public byte[] getDocumento() {
        return documento;
    }

    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }

    public TipoContrato getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(TipoContrato idTipo) {
        this.idTipo = idTipo;
    }

    public Contratante getIdContratante() {
        return idContratante;
    }

    public void setIdContratante(Contratante idContratante) {
        this.idContratante = idContratante;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }
}
