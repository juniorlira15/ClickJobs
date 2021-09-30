package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArraySet;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.Permissoes;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;

import java.util.ArrayList;
import java.util.List;

public class CadastrarServicoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTitulo,edtDescricao;
    private CurrencyEditText edtValor;
    private Spinner spnCategoria;
    private Spinner spnEstado;
    private ImageView img1,img2,img3;
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
        };
    private StorageReference storage;
    private ArrayList<String> listaURLFotos;
    private Anuncio anuncio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_servico);

        Toolbar toolbar = findViewById(R.id.toolbarCadServico);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializaViews();

        carregaDadosString();

        Permissoes.validarPermissoes(permissoes,this,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão Negadas");
        builder.setMessage("Para utilizar o App é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    private void inicializaViews() {

        edtTitulo = findViewById(R.id.edtTituloCadServ);
        edtDescricao = findViewById(R.id.edtDescricaoCadServ);
        edtValor = findViewById(R.id.editValorCadServ);
        spnCategoria = findViewById(R.id.spnCategoria);
        spnEstado = findViewById(R.id.spnUF);
        img1 = findViewById(R.id.img1CadServ);
        img2 = findViewById(R.id.img2CadServ);
        img3 = findViewById(R.id.img3CadServ);

        storage = ConfiguracaoFirebase.getFirebaseStorage();
        listaURLFotos = new ArrayList<>();
    }

    public void salvarAnuncio(View view){

        String categoria = spnCategoria.getSelectedItem().toString();
        String UF = spnEstado.getSelectedItem().toString();
        String titulo = edtTitulo.getText().toString();
        String descricao = edtDescricao.getText().toString();
        String valor = String.valueOf(edtValor.getRawValue());


        if(validaCampos()){

            abreDialog();

            anuncio = new Anuncio();
            anuncio.setTitulo(titulo);
            anuncio.setEndereco(UF);
            anuncio.setCategoria(categoria);
            anuncio.setDescricao(descricao);
            anuncio.setValor(valor);

            for (int i=0; i < listaFotosRecuperadas.size(); i++){
                String urlImagem = listaFotosRecuperadas.get(i);
                int contador = i+1;
                salvarFotoStorage(urlImagem,i,contador);
            }



        }

    }

    private void abreDialog(){
        View viewTitle = View.inflate(this,R.layout.custom_title_dialog,null);
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Salvando serviço...");
        builder.setCancelable(false);
//        builder.setCustomTitle(viewTitle);
//        builder.setTitle("Gravando Serviço...");
        builder.setView(R.layout.layout_alert_dialog);
        alertDialog = builder.create();
        alertDialog.show();


    }


    private void salvarFotoStorage(String urlString,int id,int contador){

        //Criar nÃ³ no storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child( anuncio.getIdAnuncio() )
                .child("imagem"+id);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile( Uri.parse(urlString) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlConvertida = uri.toString();
                        listaURLFotos.add( urlConvertida );
                        if(contador == listaFotosRecuperadas.size()){

                            anuncio.setFoto(listaURLFotos);
                            gravaAnuncioFirebase();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //exibirMensagemErro("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
                alertDialog.dismiss();
            }
        });

    }

    private void gravaAnuncioFirebase(){

        FirebaseUser firebaseUser = UsuarioFirebase.getFirebaseUser();
        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();

        // GRAVANDO NO ANUNCIOS GERAL
        reference.child("anuncios").child(anuncio.getEndereco()).child(anuncio.getCategoria()).child(anuncio.getIdAnuncio()).setValue(anuncio)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            //Toast.makeText(CadastrarServicoActivity.this, "Serviço inserido com sucesso!", Toast.LENGTH_SHORT).show();
                            //finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
                Toast.makeText(CadastrarServicoActivity.this, "Um erro inesperado ocorreu durante o cadastro.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        // GRAVANDO NOS ANUNCIOS DE CADA USUARIO
        reference.child("meus_anuncios").child(firebaseUser.getUid()).child(anuncio.getIdAnuncio()).setValue(anuncio).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CadastrarServicoActivity.this, "Serviço inserido com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
                Toast.makeText(CadastrarServicoActivity.this, "Um erro inesperado ocorreu durante o cadastro.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    public boolean validaCampos(){
        if(!edtTitulo.getText().toString().equals("")){
            if(!edtDescricao.getText().toString().equals("")){
                if(!String.valueOf(edtValor.getRawValue()).equals("")){
                    if(listaFotosRecuperadas.size() != 0){
                        return true;
                    }else {
                        Toast.makeText(CadastrarServicoActivity.this, "Escolha pelo menos 1 Foto", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    edtValor.setError("Preencha o campo");
                    return false;
                }
            }else{
                edtDescricao.setError("Preencha o campo");
                return false;
            }
        }else{
            edtTitulo.setError("Preencha o campo");
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.img1CadServ) : escolherImagem(1); break;
            case(R.id.img2CadServ) : escolherImagem(2);break;
            case(R.id.img3CadServ) : escolherImagem(3);break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,requestCode);
    }

    private void carregaDadosString(){

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnEstado.setAdapter(adapter);

        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCategoria.setAdapter(adapterCategoria);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == Activity.RESULT_OK){

            // RECUPERAR IMAGEM
            Uri imagemSelecionada = data.getData();
            String caminhaImagem = imagemSelecionada.toString();

            if (requestCode == 1) {
                img1.setImageURI(imagemSelecionada);
            }else if(requestCode == 2){
                img2.setImageURI(imagemSelecionada);
            }else if(requestCode == 3){
                img3.setImageURI(imagemSelecionada);
            }

            listaFotosRecuperadas.add(caminhaImagem);


        }


    }
}