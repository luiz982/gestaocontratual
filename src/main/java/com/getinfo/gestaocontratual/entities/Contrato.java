package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_contrato")
    private Integer idContrato;

    @Column
    private Integer numContrato;

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
    private String cnpj;

    @Lob
    @Column
    private byte[] documento;

    @ManyToOne
    @JoinColumn(name = "id_tipo", referencedColumnName = "id_tipo", nullable = false)
    private TipoContrato idTipo;

    @ManyToOne
    @JoinColumn(name = "id_contratante", referencedColumnName = "id_contratante", nullable = false)
    private Contratante idContratante;
}
