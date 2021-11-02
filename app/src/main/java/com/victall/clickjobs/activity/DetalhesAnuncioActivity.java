package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.Permissoes;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.Endereco;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalhesAnuncioActivity extends AppCompatActivity {

    private Anuncio anuncio;
    private TextView txtDesc,txtCategoria,txtValor,txtEndereco,txtTitulo,txtNome,txtTellefone,txtServico;
    private Toolbar mToolbar;
    private ImageSlider slider;
    private Button btnAtualizar;
    private CircleImageView imgAnunciante;
    private String[] permissoes = new String[]{
            Manifest.permission.CALL_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_anuncio);

        Bundle bundle = getIntent().getExtras();


        inicializaToolbar();

        btnAtualizar = findViewById(R.id.btnChat);
        txtDesc = findViewById(R.id.txtDescricaoServico);
        txtCategoria = findViewById(R.id.txtCategoria);
        txtEndereco = findViewById(R.id.txtEndAnuncio);
        txtValor = findViewById(R.id.txtValor);
        txtTitulo = findViewById(R.id.txtChatNomeAnunciante);
        slider = findViewById(R.id.imageSliderDetalhesAnuncio);
        txtNome = findViewById(R.id.txtNomeAnunciante);
        imgAnunciante = findViewById(R.id.imgAnuncianteDetalhesAnuncio);
        txtTellefone = findViewById(R.id.txtTelefoneDetAnunc);
        txtServico = findViewById(R.id.txtServico);


        if (bundle != null) {

            anuncio = (Anuncio) bundle.getSerializable("anuncio");
            this.anuncio = anuncio;

            txtValor.setText(anuncio.getValor());
            txtCategoria.setText(anuncio.getCategoria());
            txtDesc.setText(anuncio.getDescricao());
            txtTitulo.setText(anuncio.getTitulo());
            txtNome.setText(anuncio.getNomeAnunciante());
            txtServico.setText(anuncio.getTitulo());

            String idAnunciante = anuncio.getIdAnunciante();
            DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
            DatabaseReference fotoAnunciante = reference.child("usuarios").child(idAnunciante).child("foto");
            DatabaseReference enderecoAnunciante = reference.child("enderecos").child(idAnunciante);
            DatabaseReference telefoneAnunciante = reference.child("usuarios").child(idAnunciante).child("telefone");

            enderecoAnunciante.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Endereco endereco = snapshot.getValue(Endereco.class);
                    if(endereco!=null){
                        String bairro = endereco.getBairro();
                        if(bairro == null){
                            bairro = "";
                        }
                        txtEndereco.setText(bairro+","+endereco.getCidade()+" - "+endereco.getCep());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            fotoAnunciante.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String urlFoto = snapshot.getValue(String.class);
                    if(urlFoto!=null && !urlFoto.equals("")){
                        Picasso.get().
                                load(urlFoto)
                                .placeholder(R.drawable.img_placeholder)
                                .error(R.drawable.img_placeholder_error)
                                .into(imgAnunciante);
                    }else{
                        Picasso.get().load(R.drawable.icon_perfil).into(imgAnunciante);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            telefoneAnunciante.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String telefone = snapshot.getValue(String.class);
                    if(!telefone.isEmpty()){
                        txtTellefone.setText(telefone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            if (!fotoAnunciante.isEmpty()) {
//                Picasso.get().load(fotoAnunciante).into(imgAnunciante);
//            }else{
//                Picasso.get().load(R.drawable.icon_perfil).into(imgAnunciante);
//            }

            List<SlideModel> slideModelList = new ArrayList<>();

            for(int i=0; i<anuncio.getFoto().size();i++){
                slideModelList.add(new SlideModel(anuncio.getFoto().get(i), ""));
            }


            slider.setImageList(slideModelList,true);


            if(idAnunciante.equals(UsuarioFirebase.getFirebaseUser().getUid())){
                btnAtualizar.setVisibility(View.GONE);
            }


        }else{
            Toast.makeText(this, "Erro ao consultar informações.", Toast.LENGTH_SHORT).show();
        }

        Permissoes.validarPermissoes(permissoes,this,2);



    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("anuncio",anuncio);



    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        if(savedInstanceState!=null) {
            anuncio = (Anuncio) savedInstanceState.getSerializable("anuncio");

            txtValor.setText(anuncio.getValor());
            txtCategoria.setText(anuncio.getCategoria());
            txtDesc.setText(anuncio.getDescricao());
            txtTitulo.setText(anuncio.getTitulo());
            txtNome.setText(anuncio.getNomeAnunciante());
            txtServico.setText(anuncio.getTitulo());

            String idAnunciante = anuncio.getIdAnunciante();
        }

    }

    public void abrirImagem(View vIew){

    }

    public void inicializaToolbar(){
        mToolbar = findViewById(R.id.toolbarDetalhesAnuncio);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    public void abrirChat(View view){

        Intent intent = new Intent(DetalhesAnuncioActivity.this,ChatActivity.class);
        intent.putExtra("anuncio",anuncio);
        startActivity(intent);

    }

    public void ligarAnunciante(View view) {

        String telefone = txtTellefone.getText().toString();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telefone));
        startActivity(intent);

    }



}