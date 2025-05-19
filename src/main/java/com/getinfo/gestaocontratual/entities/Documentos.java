package com.getinfo.gestaocontratual.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "documentos")
public class Documentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long idDocumento;

    @ManyToOne
    @JoinColumn(name = "id_contrato", nullable = false, referencedColumnName = "id_contrato")
    private Contrato idContrato;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "url", nullable = false)
    private String url;

    public Long getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Long idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Contrato getContrato() {
        return idContrato;
    }

    public void setContrato(Contrato contrato) {
        this.idContrato = contrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
