package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "contratante")
public class Contratante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contratante")
    private Integer idContratante;

    @Column(nullable = false)
    private String cnpj;

    @Column(nullable = false)
    private String razaoSocial;

    @Column(nullable = false)
    private String nomeFantasia;

    @Column
    private String inscricaoEstadual;

    @Column
    private String inscricaoMunicipal;

    @Column
    private String emailCorporativo;

    @Column
    private String site;

    @Column
    private LocalDate dataFundacao;

    @Column
    private String telefone;

    @Column
    private String telefoneFixo;

    @Column
    private String cep;

    @Column
    private String bairro;

    @Column
    private String rua;

    @Column
    private String cidade;

    @Column
    private String estado;

    // NOVO CAMPO REGIÃO
    @Column
    private String regiao;

    // NOVO CAMPO TIPO EMPRESA (0 = Privada, 1 = Pública)
    @Column
    private Integer tipoEmpresa;

    @Column
    private String banco;

    @Column
    private String agencia;

    // Responsável Legal
    @Column
    private String responsavelLegalCpf;

    @Column
    private String responsavelLegalNome;

    @Column
    private String responsavelLegalTelefone;

    @Column
    private String responsavelLegalEmail;

    // Método que calcula a regiã automaticamente antes de salvar/atualizaro
    @PrePersist
    @PreUpdate
    public void calcularRegiao() {
        this.regiao = determinarRegiaoPorEstado(this.estado);
    }

    // Método privado para determinar região baseada no estado
    private String determinarRegiaoPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return null;
        }

        switch (estado.toUpperCase().trim()) {
            // Norte
            case "AC": case "AP": case "AM": case "PA":
            case "RO": case "RR": case "TO":
                return "Norte";

            // Nordeste
            case "AL": case "BA": case "CE": case "MA":
            case "PB": case "PE": case "PI": case "RN": case "SE":
                return "Nordeste";

            // Centro-Oeste
            case "GO": case "MT": case "MS": case "DF":
                return "Centro-Oeste";

            // Sudeste
            case "ES": case "MG": case "RJ": case "SP":
                return "Sudeste";

            // Sul
            case "PR": case "RS": case "SC":
                return "Sul";

            default:
                return null;
        }
    }

    // Método utilitário para verificar se é empresa pública
    public boolean isEmpresaPublica() {
        return tipoEmpresa != null && tipoEmpresa == 1;
    }

    // Método utilitário para verificar se é empresa privada
    public boolean isEmpresaPrivada() {
        return tipoEmpresa != null && tipoEmpresa == 0;
    }

    // Getters e Setters existentes
    public Integer getIdContratante() {
        return idContratante;
    }

    public void setIdContratante(Integer idContratante) {
        this.idContratante = idContratante;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public String getEmailCorporativo() {
        return emailCorporativo;
    }

    public void setEmailCorporativo(String emailCorporativo) {
        this.emailCorporativo = emailCorporativo;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public LocalDate getDataFundacao() {
        return dataFundacao;
    }

    public void setDataFundacao(LocalDate dataFundacao) {
        this.dataFundacao = dataFundacao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTelefoneFixo() {
        return telefoneFixo;
    }

    public void setTelefoneFixo(String telefoneFixo) {
        this.telefoneFixo = telefoneFixo;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // GETTER E SETTER PARA REGIÃO
    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    // GETTER E SETTER PARA TIPO EMPRESA
    public Integer getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(Integer tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getResponsavelLegalCpf() {
        return responsavelLegalCpf;
    }

    public void setResponsavelLegalCpf(String responsavelLegalCpf) {
        this.responsavelLegalCpf = responsavelLegalCpf;
    }

    public String getResponsavelLegalNome() {
        return responsavelLegalNome;
    }

    public void setResponsavelLegalNome(String responsavelLegalNome) {
        this.responsavelLegalNome = responsavelLegalNome;
    }

    public String getResponsavelLegalTelefone() {
        return responsavelLegalTelefone;
    }

    public void setResponsavelLegalTelefone(String responsavelLegalTelefone) {
        this.responsavelLegalTelefone = responsavelLegalTelefone;
    }

    public String getResponsavelLegalEmail() {
        return responsavelLegalEmail;
    }

    public void setResponsavelLegalEmail(String responsavelLegalEmail) {
        this.responsavelLegalEmail = responsavelLegalEmail;
    }
}