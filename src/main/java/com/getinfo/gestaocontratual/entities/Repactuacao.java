package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "repactuacao")
public class Repactuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_repactuacao")
    private Long idRepactuacao;

    @ManyToOne
    @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato", nullable = false)
    private Contrato idContrato;

    @Column(name = "dt_fim_contrato")
    @Temporal(TemporalType.DATE)
    private Date dtFimContrato;

    @Column(nullable = false)
    private String nome;



    @Column
    private String descricao;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    // Construtores
    public Repactuacao() {}

    public Repactuacao(Contrato idContrato, Date dtFimContrato, String nome, String descricao) {
        this.idContrato = idContrato;
        this.dtFimContrato = dtFimContrato;
        this.nome = nome;

        this.descricao = descricao;
    }

    // Getters e Setters
    public Long getIdRepactuacao() {
        return idRepactuacao;
    }

    public void setIdRepactuacao(Long idRepactuacao) {
        this.idRepactuacao = idRepactuacao;
    }

    public Contrato getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Contrato idContrato) {
        this.idContrato = idContrato;
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