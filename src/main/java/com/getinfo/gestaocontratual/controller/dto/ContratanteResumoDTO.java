package com.getinfo.gestaocontratual.controller.dto;

public class ContratanteResumoDTO {

    private String nomeFantasia;
    private String cnpj;
    private long contratosAtivos;

    public ContratanteResumoDTO(String nomeFantasia, String cnpj, long contratosAtivos) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.contratosAtivos = contratosAtivos;
    }

    // Getters and Setters
    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public long getContratosAtivos() {
        return contratosAtivos;
    }

    public void setContratosAtivos(long contratosAtivos) {
        this.contratosAtivos = contratosAtivos;
    }
}