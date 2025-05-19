package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

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

    @Column(nullable = false)
    private TipoContrato tipoContrato;

    @Column
    private String tipoServico;

    @Column(name = "created_at")
    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "id_contratante", referencedColumnName = "id_contratante", nullable = false)
    private Contratante idContratante;

    @Column(nullable = false)
    private String responsavel;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContratoColaborador> colaboradores;

    @ManyToOne
    @JoinColumn(name = "id_status", referencedColumnName = "id_status")
    private Status status;

    public List<ContratoColaborador> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(List<ContratoColaborador> colaboradores) {
        this.colaboradores = colaboradores;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTipoServico() { return tipoServico; }

    public void setTipoServico(String tipoServico) { this.tipoServico = tipoServico; }

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

    public Contratante getIdContratante() {
        return idContratante;
    }

    public void setIdContratante(Contratante idContratante) {
        this.idContratante = idContratante;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getResponsavel() { return responsavel; }

    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
}
