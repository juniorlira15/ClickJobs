package com.victall.clickjobs.model;

import com.google.firebase.database.DatabaseReference;
import com.victall.clickjobs.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Anuncio implements Serializable {

    private ArrayList<String> foto;
    private String titulo;
    private String categoria;
    private String valor;
    private String endereco;
    private String descricao;
    private String data;
    private String hora;
    private String idAnuncio;
    private String idAnunciante;
    private String nomeAnunciante;
    private String fotoAnunciante;


    {
        data = recuperaData();
        hora = recuperaHora();
    }


    public Anuncio() {
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getDatabaseReference().child("meus_anuncios");
        setIdAnuncio(anuncioRef.push().getKey());
    }

    public String getFotoAnunciante() {
        return fotoAnunciante;
    }

    public void setFotoAnunciante(String fotoAnunciante) {
        this.fotoAnunciante = fotoAnunciante;
    }

    public String getIdAnunciante() {
        return idAnunciante;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setIdAnunciante(String idAnunciante) {
        this.idAnunciante = idAnunciante;
    }

    public String getNomeAnunciante() {
        return nomeAnunciante;
    }

    public void setNomeAnunciante(String nomeAnunciante) {
        this.nomeAnunciante = nomeAnunciante;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public ArrayList<String> getFoto() {
        return foto;
    }

    public void setFoto(ArrayList<String> foto) {
        this.foto = foto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String recuperaData(){

        //Recupera a data atual
        Date dataAtual = Calendar.getInstance().getTime();

        //Inicializa o SimpleDateFormat passando o formato no construtor
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //utilizaca o objeto dateFormat com o método format para formatar a data atual criada
        String data = dateFormat.format(dataAtual);


        return data;
    }

    public String recuperaHora(){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        String dataFormatada = sdf.format(hora);

        return dataFormatada;
    }
}
