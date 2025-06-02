package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;

import java.time.temporal.TemporalAccessor;
import java.util.Date;

@Entity
@Table(name = "aditivos_contrato")
public class AditivoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aditivo")
    private Long idAditivo;

    @ManyToOne
    @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato", nullable = false)
    private Contrato contrato;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] documento;

    @Column
    private String descricao;

    @Column(name = "data_vigencia")
    private Date dataVigencia;

    @Column
    private String justificativa;

    @Column(name = "tipo_aditivo")
    private String tipoAditivo;


    public Long getIdAditivo() {
        return idAditivo;
    }

    public void setIdAditivo(Long idAditivo) {
        this.idAditivo = idAditivo;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataVigencia() {
        return dataVigencia;
    }

    public void setDataVigencia(Date dataVigencia) {
        this.dataVigencia = dataVigencia;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getTipoAditivo() {
        return tipoAditivo;
    }

    public void setTipoAditivo(String tipoAditivo) {
        this.tipoAditivo = tipoAditivo;
    }

    public byte[] getDocumento() {
        return documento;
    }

    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }
}
