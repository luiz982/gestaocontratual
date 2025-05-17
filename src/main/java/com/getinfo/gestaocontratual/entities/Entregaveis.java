package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "entregaveis")
public class Entregaveis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Entregavel")
    private int idEntregavel;

    @ManyToOne
    @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato", nullable = false)
    private Contrato idContrato;

    @Column(name = "Nome", length = 100)
    private String nome;

    @Column(name = "Dt_Inicio")
    private Date dtInicio;

    @Column(name = "Dt_Fim", nullable = true)
    private Date dtFim;

    @OneToMany(mappedBy = "entregavel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntregaveisColaborador> colaboradores;

    @Column(name = "Status")
    private StatusEntregavel status;

    public Contrato getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Contrato idContrato) {
        this.idContrato = idContrato;
    }

    // Getters e Setters
    public int getIdEntregavel() {
        return idEntregavel;
    }

    public void setIdEntregavel(int idEntregavel) {
        this.idEntregavel = idEntregavel;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public StatusEntregavel getStatus() {
        return status;
    }

    public void setStatus(StatusEntregavel status) {
        this.status = status;
    }   

    public List<EntregaveisColaborador> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(List<EntregaveisColaborador> colaboradores) {
        this.colaboradores = colaboradores;
    }
}
