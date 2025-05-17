package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;

import java.lang.reflect.Type;

@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_status")
    private Long idStatus;

    @Column(name="nome")
    private String nome;

    @Column(name="descricao")
    private String descricao;

    public Long getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Long idStatus) {
        this.idStatus = idStatus;
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
}
