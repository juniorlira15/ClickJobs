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
    private final String CHAVE_LOGRADOURO = "user.logradouro";
    private final String CHAVE_CEP = "user.cep";
    private final String CHAVE_CIDADE = "user.cidade";
    private final String CHAVE_ESTADO = "user.estado";
    private final String CHAVE_PAIS = "user.pais";
    private final String CHAVE_BAIRRO = "user.pais";
    private final String CHAVE_NUMERO_END = "user.numero.end";
    private final String CHAVE_COMPLEMENTO = "user.complento";


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
    public String getCHAVE_LOGRADOURO(){
        return preferences.getString("CHAVE_LOGRADOURO","Atualizar");
    }
    public String getCHAVE_CEP(){
        return preferences.getString("CHAVE_CEP","Atualizar");
    }
    public String getCHAVE_CIDADE(){
        return preferences.getString("CHAVE_CIDADE","Atualizar");
    }
    public String getCHAVE_ESTADO(){
        return preferences.getString("CHAVE_ESTADO","Atualizar");
    }
    public String getCHAVE_PAIS(){
        return preferences.getString("CHAVE_PAIS","Atualizar");
    }
    public String getCHAVE_BAIRRO(){
        return preferences.getString("CHAVE_BAIRRO","Atualizar");
    }
    public String getCHAVE_NUMERO_END(){
        return preferences.getString("CHAVE_NUMERO_END","Atualizar");
    }
    public String getCHAVE_COMPLEMENTO(){
        return preferences.getString("CHAVE_COMPLEMENTO","Atualizar");
    }
    public void setCHAVE_LOGRADOURO(String logradouro){
        editor.putString(CHAVE_LOGRADOURO,logradouro);
        editor.commit();
    }
    public void setCHAVE_CEP(String cep){
        editor.putString(CHAVE_CEP,cep);
        editor.commit();
    }
    public void setCHAVE_CIDADE(String cidade){
        editor.putString(CHAVE_CIDADE,cidade);
        editor.commit();
    }
    public void setCHAVE_ESTADO(String estado){
        editor.putString(CHAVE_ESTADO,estado);
        editor.commit();
    }
    public void setCHAVE_PAIS(String pais){
        editor.putString(CHAVE_PAIS,pais);
        editor.commit();
    }
    public void setCHAVE_BAIRRO(String bairro){
        editor.putString(CHAVE_BAIRRO,bairro);
        editor.commit();
    }
    public void setCHAVE_NUMERO_END(String numero_end){
        editor.putString(CHAVE_NUMERO_END,numero_end);
        editor.commit();
    }
    public void setCHAVE_COMPLEMENTO(String complemento){
        editor.putString(CHAVE_COMPLEMENTO,complemento);
        editor.commit();
    }

}
