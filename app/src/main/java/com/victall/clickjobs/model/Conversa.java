package com.victall.clickjobs.model;

public class Conversa {


    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Anuncio anuncio;
    private String horario;


    public Conversa() {
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Anuncio getUsuario() {
        return anuncio;
    }

    public void setUsuario(Anuncio anuncio) {
        this.anuncio = anuncio;
    }
}
