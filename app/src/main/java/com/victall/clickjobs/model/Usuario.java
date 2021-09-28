package com.victall.clickjobs.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class Usuario {



    private String nome;
    private String email;
    private String sobrenome;
    private String telefone;
    private String senha;
    private String id;
    private String foto;
    private String ultimaAtualizacao;

    {
        this.setFoto("");
    }

    public String getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(String ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public String getFoto() {
        return (foto);
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @NonNull
    public String getId() {
        return id;
    }
    @NonNull
    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    public String getNome() {
        return nome;
    }
    @NonNull
    public void setNome(String nome) {
        this.nome = nome;
    }
    @NonNull
    public String getEmail() {
        return email;
    }
    @NonNull
    public void setEmail(String email) {
        this.email = email;
    }
    @NonNull
    public String getSobrenome() {
        return sobrenome;
    }
    @NonNull
    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }
    @NonNull
    public String getTelefone() {
        return telefone;
    }
    @NonNull
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


}
