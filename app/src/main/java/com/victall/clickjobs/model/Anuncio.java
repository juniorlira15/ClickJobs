package com.victall.clickjobs.model;

import com.google.firebase.database.DatabaseReference;
import com.victall.clickjobs.config.ConfiguracaoFirebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Anuncio {

    private ArrayList<String> foto;
    private String titulo;
    private String categoria;
    private String valor;
    private String endereco;
    private String descricao;
    private String data;
    private String idAnuncio;

    {
        data = recuperaData();
    }


    public Anuncio() {
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getDatabaseReference().child("meus_anuncios");
        setIdAnuncio(anuncioRef.push().getKey());
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
}
