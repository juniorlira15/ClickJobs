package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.model.Endereco;

public class ConfirmaEnderecoActivity extends AppCompatActivity {

    private TextInputEditText edtRua,edtNumero,edtComplemento,edtBairro,edtCidade,edtEstado,edtCep,edtPais;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_endereco);

        inicializaViews();

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            Endereco endereco = (Endereco) bundle.getSerializable("endereco");
            idUsuario = bundle.getString("id");

            preecheCampos(endereco);

        }


    }

    private void preecheCampos(Endereco endereco) {

        String[] items = endereco.getLogradouro().split(",");

        String rua = "";
        String numero = "";
        String bairro = "";
        String cidade = "";
        String estado = "";
        String pais = "";
        String cep = "";

        for (int i = 0; i<items.length; i++)
        {
            if(i==0){
                rua = items[0];
            }
            if(i==1){
                // dividir NUmero e Bairro
                String[] numeroBairro = items[1].split("-");
                numero = numeroBairro[0];
                bairro = numeroBairro[1];
            }
            if(i==2){
                // dividir Cidade e Estado
                String[] cidadeEstado = items[2].split("-");
                cidade = cidadeEstado[0];
                estado = cidadeEstado[1];
            }
            if(i==3){
                cep = items[3];
            }
            if(i==4){
                pais = items[4];
            }
        }

        edtRua.setText(rua);
        edtNumero.setText(numero.replace(" ",""));
        edtComplemento.setText("");
        edtBairro.setText(bairro);
        edtCidade.setText(cidade);
        edtEstado.setText(estado.replace(" ",""));
        edtPais.setText(pais);
        edtCep.setText(cep.replace(" ",""));



    }

    private void inicializaViews() {

        edtBairro = findViewById(R.id.edtBairro);
        edtCep = findViewById(R.id.edtCep);
        edtCidade = findViewById(R.id.edtCidade);
        edtComplemento = findViewById(R.id.edtComplemento);
        edtEstado = findViewById(R.id.edtEstado);
        edtNumero = findViewById(R.id.edtNumero);
        edtPais = findViewById(R.id.edtPais);
        edtRua = findViewById(R.id.edtRua);
    }

    public void gravarEndereco(View view){

        Endereco endereco = new Endereco();

        endereco.setLogradouro(edtRua.getText().toString());
        endereco.setNumero(edtNumero.getText().toString());
        if(edtComplemento.getText().toString().isEmpty()) {
            endereco.setComplemento("");
        }else{
            endereco.setComplemento(edtComplemento.getText().toString());
        }
        endereco.setCidade(edtCidade.getText().toString());
        endereco.setCep(edtCep.getText().toString());
        endereco.setEstado(edtEstado.getText().toString());
        endereco.setPais(edtPais.getText().toString());

        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference referenceEnd = reference.child("enderecos").child(idUsuario);

        referenceEnd.setValue(endereco).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(ConfirmaEnderecoActivity.this, "Endereco atualizado com sucesso.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConfirmaEnderecoActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}