
package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;

public class DetalheMeuAnuncioActivity extends AppCompatActivity {

    private TextView titulo,valor,endereco;
    private Button btnAlterar,btnExcluir;
    private Anuncio anuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_meu_anuncio);

        Toolbar toolbar = findViewById(R.id.toolbarDetalheMeuAnuncio);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializaViews();

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            anuncio = (Anuncio) bundle.getSerializable("anuncio");

            titulo.setText(anuncio.getTitulo());
            valor.setText(anuncio.getValor());
            endereco.setText(anuncio.getEndereco());

        }


        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletarAnuncio(anuncio);
                finish();
            }
        });




    }


    public void deletarAnuncio(Anuncio anuncio){

        FirebaseUser firebaseUser = UsuarioFirebase.getFirebaseUser();
        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
        reference.child("meus_anuncios").child(firebaseUser.getUid()).child(anuncio.getIdAnuncio())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(DetalheMeuAnuncioActivity.this, "Anúncio removido com Sucesso..", Toast.LENGTH_SHORT).show();
            }
        });


        reference.child("anuncios").child(anuncio.getEndereco()).child(anuncio.getCategoria())
                .child(anuncio.getIdAnuncio())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(DetalheMeuAnuncioActivity.this, "Anúncio removido com Sucesso..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void inicializaViews(){

        btnAlterar = findViewById(R.id.btnAlterar);
        btnExcluir = findViewById(R.id.btnExcluir);
        titulo = findViewById(R.id.txtTituloMeuAnuncio);
        valor = findViewById(R.id.txtValorMeuAnuncio);
        endereco = findViewById(R.id.txtEnderecoMeuAnuncio);

    }
}