package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;


@Entity
@Table(name = "contrato_colaboradores")
public class ContratoColaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaborador colaborador;

    @Column(name = "funcao_contrato")
    private String funcaoContrato;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public String getFuncaoContrato() {
        return funcaoContrato;
    }

    public void setFuncaoContrato(String funcaoContrato) {
        this.funcaoContrato = funcaoContrato;
    }
}
