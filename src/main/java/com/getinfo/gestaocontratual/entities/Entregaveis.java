package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "entregaveis")
public class Entregaveis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Entregavel")
    private int idEntregavel;

    // @ManyToOne
    // @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato", nullable = false)
    @Column(name = "id_contrato")
    private long idContrato; 

    @Column(name = "Nome", length = 100)
    private String nome;

    @Column(name = "Dt_Inicio")
    private Date dtInicio;

    @Column(name = "Dt_Fim")
    private Date dtFim;

    @Column(name = "Status")
    private boolean status;
        
    // Getters e Setters
    public int getIdEntregavel() {
        return idEntregavel;
    }

    public void setIdEntregavel(int idEntregavel) {
        this.idEntregavel = idEntregavel;
    }

    public long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(long idContrato) {
        this.idContrato = idContrato;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }    
}
