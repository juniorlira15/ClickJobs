package com.victall.clickjobs.model;

import java.io.Serializable;

public class Endereco implements Serializable {

    private String logradouro;
    private String bairro;
    private String numero;
    private String cidade;
    private String cep;
    private String complemento;
    private String latitude;
    private String longitude;
    private String estado;
    private String pais;


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }


    public static double calculateDistance(double latPoint1, double lngPoint1,
                             double latPoint2, double lngPoint2) {
        if(latPoint1 == latPoint2 && lngPoint1 == lngPoint2) {
            return 0d;
        }

        final double EARTH_RADIUS = 6371.0; //km value;

        //converting to radians
        latPoint1 = Math.toRadians(latPoint1);
        lngPoint1 = Math.toRadians(lngPoint1);
        latPoint2 = Math.toRadians(latPoint2);
        lngPoint2 = Math.toRadians(lngPoint2);

        double distance = Math.pow(Math.sin((latPoint2 - latPoint1) / 2.0), 2)
                + Math.cos(latPoint1) * Math.cos(latPoint2)
                * Math.pow(Math.sin((lngPoint2 - lngPoint1) / 2.0), 2);
        distance = 2.0 * EARTH_RADIUS * Math.asin(Math.sqrt(distance));

        return distance; //km value
    }


}
