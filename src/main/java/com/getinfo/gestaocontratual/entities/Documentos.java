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

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Lob
    @Column(name = "conteudo", columnDefinition = "LONGBLOB")
    private byte[] conteudo;

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

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }
}
