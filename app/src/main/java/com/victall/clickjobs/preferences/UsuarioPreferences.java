package com.victall.clickjobs.preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class UsuarioPreferences {

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final String NOME_ARQUIVO = "usuario.preferencias";
    private final String CHAVE_NOME = "user_name";
    private final String CHAVE_EMAIL = "user_email";
    private final String CHAVE_SOBRENOME = "user_sobrenome";
    private final String CHAVE_TELEFONE = "user_telefone";
    private final String CHAVE_FOTO = "user_foto";
    private final String CHAVE_ID_USUARIO = "user_id";
    private static boolean PERFIL_ON_DB = false;
    private final String CHAVE_TOKEN = "user_token";


    public UsuarioPreferences(Context c) {
        this.context = c;
        preferences = context.getSharedPreferences(NOME_ARQUIVO,0);
        editor = preferences.edit();
    }
    public void salvarNomeUsuario(String nome){
        editor.putString(CHAVE_NOME,nome);
        editor.commit();
    }
    public String getNomeUsuario(){
        return preferences.getString(CHAVE_NOME,"");
    }
    public String getNomeUsuarioCompleto(){

        String nome = getNomeUsuario();
        String sobreNome = getSobrenomeUsuario();

        String nomeCompleto = nome+" "+sobreNome;

        return nomeCompleto;
    }
    public void salvarSobrenome(String sobrenome){
        editor.putString(CHAVE_SOBRENOME,sobrenome);
        editor.commit();
    }
    public String getSobrenomeUsuario(){
        return preferences.getString(CHAVE_SOBRENOME,"");
    }
    public void salvarEmailUsuario(String email){
        editor.putString(CHAVE_EMAIL,email);
        editor.commit();
    }
    public String getEmailUsuario(){
        return preferences.getString(CHAVE_EMAIL,"");
    }
    public void salvarTelefoneUsuario(String telefone){
        editor.putString(CHAVE_TELEFONE,telefone);
        editor.commit();
    }
    public String getTelefoneUsuario(){
        return preferences.getString(CHAVE_TELEFONE,"");
    }
    public void salvarFotoUsuario(String fotoPath){
        editor.putString(CHAVE_FOTO,fotoPath);
        editor.commit();
    }
    public String getFotoUsuario(){
        return preferences.getString(CHAVE_FOTO,"");
    }

    public void salvarIdUsuario(String id){
        editor.putString(CHAVE_ID_USUARIO,id);
        editor.commit();
    }
    public String getIdUsuario(){
        return preferences.getString(CHAVE_ID_USUARIO,"");
    }
    public void setUpdatePerfil(boolean b){
        PERFIL_ON_DB = b;
    }
    public boolean getStatusPerfilUpdate(){
        return PERFIL_ON_DB;
    }
    public void setTokenUsuario(String token){
        editor.putString(CHAVE_TOKEN,token);
        editor.commit();
    }
    public String getTokenUsuario(){
        return preferences.getString(CHAVE_TOKEN,"");
    }
    public void deletePreferences(){
        editor.putString(CHAVE_NOME,"");
        editor.putString(CHAVE_EMAIL,"");
        editor.putString(CHAVE_SOBRENOME,"");
        editor.putString(CHAVE_TELEFONE,"");
        editor.putString(CHAVE_FOTO,"");
        editor.commit();
    }
}
