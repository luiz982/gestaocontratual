package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.*;

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

    @ManyToMany
    @JoinTable(
            name = "contrato_colaborador",
            joinColumns = @JoinColumn(name = "id_contrato"),
            inverseJoinColumns = @JoinColumn(name = "id_colaborador")
    )
    private Set<Colaborador> colaboradores = new HashSet<>();

    @OneToMany(mappedBy = "idContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entregaveis> entregaveis;


    @OneToMany(mappedBy = "idContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documentos> documentos;

    @ManyToOne
    @JoinColumn(name = "id_status", referencedColumnName = "id_status")
    private Status status;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContratoColaborador> contratoColaboradores = new ArrayList<>();

    public List<ContratoColaborador> getContratoColaboradores() {
        return contratoColaboradores;
    }

    public void setContratoColaboradores(List<ContratoColaborador> contratoColaboradores) {
        this.contratoColaboradores = contratoColaboradores;
    }

    public Set<Colaborador> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(Set<Colaborador> colaboradores) {
        this.colaboradores = colaboradores;
    }

    public List<Entregaveis> getEntregaveis() {
        return entregaveis;
    }

    public void setEntregaveis(List<Entregaveis> entregaveis) {
        this.entregaveis = entregaveis;
    }

    public List<Documentos> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documentos> documentos) {
        this.documentos = documentos;
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

    public String getResponsavel() { return responsavel; }

    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
}
