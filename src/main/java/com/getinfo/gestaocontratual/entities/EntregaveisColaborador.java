package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;


@Entity
@Table(name = "Entregavel_colaboradores")
public class EntregaveisColaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entregavel_id")
    private Entregaveis entregavel;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaborador colaborador;

    @Column(name = "funcao_entregavel")
    private String funcaoEntregavel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entregaveis getEntregavel() {
        return entregavel;
    }

    public void setEntregavel(Entregaveis entregavel) {
        this.entregavel = entregavel;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public String getFuncaoEntregavel() {
        return funcaoEntregavel;
    }

    public void setFuncaoEntregavel(String funcaoContrato) {
        this.funcaoEntregavel = funcaoContrato;
    }
}
