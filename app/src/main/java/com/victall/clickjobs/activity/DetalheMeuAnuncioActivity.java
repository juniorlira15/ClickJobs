
package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.CheckConnection;
import com.victall.clickjobs.help.DataHora;
import com.victall.clickjobs.help.Permissoes;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DetalheMeuAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText titulo, descricao;
    private Button btnAlterar,btnExcluir;
    private Anuncio anuncio;
    private TextView data,endereco;
    private ImageView img1,img2,img3,imgEditTit,imgEditDesc;
    private ImageView imgDelete1,imgDelete2,imgDelete3;
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private StorageReference storage;
    private ArrayList<String> listaURLFotos = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private Locale mLocale;
    private Spinner spnCategoria;
    private Spinner spnEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_meu_anuncio);

        Toolbar toolbar = findViewById(R.id.toolbarDetalheMeuAnuncio);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializaViews();

        carregaDadosString();

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            anuncio = (Anuncio) bundle.getSerializable("anuncio");

            titulo.setText(anuncio.getTitulo());
            descricao.setText(anuncio.getDescricao());
            endereco.setText(anuncio.getEndereco());
            data.setText(anuncio.getData()+" "+anuncio.getHora());
            listaFotosRecuperadas = anuncio.getFoto();

            for(int i=0; i<anuncio.getFoto().size();i++){

                if (i == 0) {
                    Picasso.get().load(anuncio.getFoto().get(i))
                            .error(R.drawable.placeholder_error)
                            .into(img1);
                }
                if (i == 1) {
                    Picasso.get().load(anuncio.getFoto().get(i))
                            .error(R.drawable.placeholder_error)
                            .into(img2);
                }
                if (i == 2) {
                    Picasso.get().load(anuncio.getFoto().get(i))
                            .error(R.drawable.placeholder_error)
                            .into(img3);
                }

            }

            String categoria = anuncio.getCategoria();
            String estado = anuncio.getEndereco();

            String[] estados = getResources().getStringArray(R.array.estados);
            String[] categorias = getResources().getStringArray(R.array.categoria);

            for(int i = 0; i < estados.length; i++){

                if(estados[i].equals(estado)){
                    spnEstado.setSelection(i);
                }

            }

            for(int i = 0; i < categorias.length; i++){

                if(categorias[i].equals(categoria)){
                    spnCategoria.setSelection(i);
                }

            }

        }


        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletarAnuncio(anuncio);

            }
        });


        Permissoes.validarPermissoes(permissoes,this,1);

        firebaseUser = UsuarioFirebase.getFirebaseUser();


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
                TelaPrincipalActivity.deleteItem(anuncio);
                TelaPrincipalActivity.adapter.notifyDataSetChanged();
                finish();
            }
        });
    }

    public void inicializaViews(){

        btnAlterar = findViewById(R.id.btnAlterar);
        btnExcluir = findViewById(R.id.btnExcluir);
        titulo = findViewById(R.id.txtTituloEditAnuncio);
        descricao = findViewById(R.id.txtEditDescAnuncio);
        endereco = findViewById(R.id.txtEndEditAnuncio);
        data = findViewById(R.id.txtDataEditAnuncio);
        img1 = findViewById(R.id.img1EditServ);
        img2 = findViewById(R.id.img2EditServ);
        img3 = findViewById(R.id.img3EditServ);
        imgEditDesc = findViewById(R.id.imgEditDesc);
        imgEditTit = findViewById(R.id.imgEditTitulo);
        imgDelete1 = findViewById(R.id.imgDelete1);
        imgDelete2 = findViewById(R.id.imgDelete2);
        imgDelete3 = findViewById(R.id.imgDelete3);
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        spnCategoria = findViewById(R.id.spnCategoriaEdit);
        spnEstado = findViewById(R.id.spnEstadoEdit);

    }

    public void alterarDados(){

        // checar se nao tem campos vazios
        if(!titulo.getText().toString().isEmpty()){
            if(!descricao.getText().toString().isEmpty()){
                 abreDialog();
                 gravaAnuncioFirebase();
            }else{
                Toast.makeText(this, "Preencha o campo Descrição.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha o campo Titulo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarFotoStorage(String urlString,int id,int contador){


        Log.d("ID_AN", "ID: "+anuncio.getIdAnuncio()+" ID"+id);
        //Criar nÃ³ no storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child( anuncio.getIdAnuncio() )
                .child("imagem"+id);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                imagemAnuncio.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlConvertida = uri.toString();
                        listaURLFotos.add(urlConvertida);
                        if (contador == listaFotosRecuperadas.size()) {
                            anuncio.setFoto(listaURLFotos);
                            gravaAnuncioFirebase();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == Activity.RESULT_OK){

            // RECUPERAR IMAGEM
            Uri imagemSelecionada = data.getData();
            String caminhaImagem = imagemSelecionada.toString();

            if (requestCode == 1) {
                Picasso.get().load(imagemSelecionada)
                        .into(img1);
                imgDelete1.setEnabled(true);
                listaFotosRecuperadas.set(0, caminhaImagem);

            }else if(requestCode == 2){
                Picasso.get().load(imagemSelecionada)
                        .into(img2);
                imgDelete2.setEnabled(true);
                listaFotosRecuperadas.set(1, caminhaImagem);

            }else if(requestCode == 3){
                Picasso.get().load(imagemSelecionada)
                        .into(img3);
                imgDelete3.setEnabled(true);
                listaFotosRecuperadas.set(2, caminhaImagem);

            }





        }


    }

    public void escolherImagem(int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,requestCode);
    }

    public static void showKeyboard(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) editText.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.img1EditServ) : escolherImagem(1);break;
            case(R.id.img2EditServ) : escolherImagem(2);break;
            case(R.id.img3EditServ) : escolherImagem(3);break;
            case(R.id.imgEditDesc) :
                descricao.setEnabled(true);
                descricao.requestFocus();
                descricao.setSelection(descricao.getText().length());
                showKeyboard(descricao);
                break;
            case(R.id.imgEditTitulo) :
                titulo.setEnabled(true);
                titulo.requestFocus();
                titulo.setSelection(titulo.getText().length());
                showKeyboard(titulo);
                break;
            case(R.id.btnAlterar) : alterarDados();break;
        }
    }

    private void gravaAnuncioFirebase(){

        if(CheckConnection.isOnline(this)) {

            DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();

            anuncio.setDescricao(descricao.getText().toString());
            anuncio.setTitulo(titulo.getText().toString());
            anuncio.setData(DataHora.recuperaData());
            anuncio.setHora(DataHora.recuperaHora());

//            HashMap<String,Object> hashMap = new HashMap<>();
//            hashMap.put("descricao",descricao.getText().toString());
//            hashMap.put("titulo",titulo.getText().toString());
//            hashMap.put("foto",listaURLFotos);

            // GRAVANDO NO ANUNCIOS GERAL
            reference.child("anuncios").child(anuncio.getEndereco()).child(anuncio.getCategoria()).child(anuncio.getIdAnuncio()).setValue(anuncio)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(DetalheMeuAnuncioActivity.this, "Serviço alterado com sucesso!", Toast.LENGTH_SHORT).show();
                                //finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alertDialog.dismiss();
                    Toast.makeText(DetalheMeuAnuncioActivity.this, "Um erro inesperado ocorreu durante o cadastro.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });


            // GRAVANDO NOS ANUNCIOS DE CADA USUARIO
            reference.child("meus_anuncios").child(firebaseUser.getUid()).child(anuncio.getIdAnuncio()).setValue(anuncio).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(DetalheMeuAnuncioActivity.this, "Serviço alterado com sucesso!", Toast.LENGTH_SHORT).show();
                    //anunciosDAO.execute(getApplicationContext());
                    alertDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alertDialog.dismiss();
                    Toast.makeText(DetalheMeuAnuncioActivity.this, "Um erro inesperado ocorreu durante o cadastro.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            // Atualizando Lista da Tela Principal
            TelaPrincipalActivity.addItem(anuncio);
            TelaPrincipalActivity.adapter.notifyDataSetChanged();

        }else{
            Toast.makeText(this, "Verifique sua conexão de internet e tente novamente.", Toast.LENGTH_SHORT).show();
        }

    }

    private void abreDialog(){
        //View viewTitle = View.inflate(this,R.layout.custom_title_dialog,null);
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Alterando serviço...");
        builder.setCancelable(false);
//        builder.setCustomTitle(viewTitle);
//        builder.setTitle("Gravando Serviço...");
        //builder.setView(R.layout.layout_alert_dialog);
        alertDialog = builder.create();
        alertDialog.show();


    }

    public void deletaImg1(View view){

        switch (view.getId()){
            case R.id.imgDelete1: Picasso.get().load(R.drawable.img_padrao).into(img1); imgDelete1.setEnabled(false); break;
            case R.id.imgDelete2: Picasso.get().load(R.drawable.img_padrao).into(img2); imgDelete2.setEnabled(false); break;
            case R.id.imgDelete3: Picasso.get().load(R.drawable.img_padrao).into(img3); imgDelete3.setEnabled(false); break;
        }

    }

    private void carregaDadosString(){

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnEstado.setAdapter(adapter);
        spnEstado.setEnabled(false);

        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCategoria.setAdapter(adapterCategoria);
        spnCategoria.setEnabled(false);
    }
}