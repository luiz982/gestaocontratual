package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "contratante")
public class Contratante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_contratante")
    private Integer idContratante;

    @Column(nullable = false)
    private String nome;

    @Column
    private String razaoSocial;

    @Column(nullable = false)
    private String cnpj;

    @Column
    private String inscricaoMunicipal;

    @Column
    private String inscricaoEstadual;
}
