package com.victall.clickjobs.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.victall.clickjobs.R;
import com.victall.clickjobs.preferences.UsuarioPreferences;

public class PerfilActivity extends AppCompatActivity {

    private UsuarioPreferences preferences;
    private TextView nome,sobrenome,telefone,email,nomeCompleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarMeuPerfil);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        inicializaViews();

        preferences = new UsuarioPreferences(this);

        nome.setText(preferences.getNomeUsuario());
        sobrenome.setText(preferences.getSobrenomeUsuario());
        email.setText(preferences.getEmailUsuario());
        telefone.setText(preferences.getTelefoneUsuario());
        nomeCompleto.setText(preferences.getNomeUsuarioCompleto()+" "+preferences.getSobrenomeUsuario());

    }

    private void inicializaViews(){
        nome = findViewById(R.id.txtNomePerfil);
        sobrenome = findViewById(R.id.txtSobrenomePerfil);
        telefone = findViewById(R.id.txtTelefonePerfil);
        email = findViewById(R.id.txtEmailPerfil);
        nomeCompleto = findViewById(R.id.txtNomeCompletoPerfil);
    }
}