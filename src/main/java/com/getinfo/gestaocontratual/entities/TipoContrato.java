package com.getinfo.gestaocontratual.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "tipo_contrato")
public class TipoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_tipo")
    private Integer idTipo;

    @Column
    private String nome;

    @Column
    private String descricao;



}
