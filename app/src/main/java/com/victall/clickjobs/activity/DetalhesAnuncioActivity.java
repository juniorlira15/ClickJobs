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
import android.util.Log;
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
import com.victall.clickjobs.help.CheckConnection;
import com.victall.clickjobs.help.Permissoes;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.Endereco;
import com.victall.clickjobs.model.Usuario;

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
    private static final String TELEFONE = "(xx) xxxx xxxx)";
    private String idAnunciante;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_anuncio);

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

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            anuncio = (Anuncio) bundle.getSerializable("anuncio");

            recuperaPerfil();

            txtValor.setText(anuncio.getValor());
            txtCategoria.setText(anuncio.getCategoria());
            txtDesc.setText(anuncio.getDescricao());
            txtTitulo.setText(anuncio.getTitulo());
            //txtNome.setText(anuncio.getNomeAnunciante());
            txtServico.setText(anuncio.getTitulo());

            idAnunciante = anuncio.getIdAnunciante();
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
                        anuncio.setEndereco(bairro+","+endereco.getCidade()+" - "+endereco.getCep());
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
                    anuncio.setFotoAnunciante(urlFoto);
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
            DatabaseReference nomeAnunciante = ConfiguracaoFirebase.getDatabaseReference().child("usuarios").child(idAnunciante).child("nome");

            nomeAnunciante.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nome = snapshot.getValue(String.class);
                    txtNome.setText(nome);
                    anuncio.setNomeAnunciante(nome);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            List<SlideModel> slideModelList = new ArrayList<>();

            for(int i=0; i<anuncio.getFoto().size();i++){
                slideModelList.add(new SlideModel(anuncio.getFoto().get(i), ""));
            }


            slider.setImageList(slideModelList,true);


            if(idAnunciante.equals(UsuarioFirebase.getFirebaseUser().getUid())){
                btnAtualizar.setVisibility(View.GONE);
            }


        }

        Permissoes.validarPermissoes(permissoes,this,2);



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
        intent.putExtra("usuario",this.usuario);
        startActivity(intent);

    }

    private void recuperaPerfil(){

        if(CheckConnection.isOnline(this)) {

            DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();

            databaseReference.child("usuarios").child(anuncio.getIdAnunciante())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot !=null) {

                                Log.d("KEY", "caminho: "+anuncio.getIdAnunciante());

                                usuario = snapshot.getValue(Usuario.class);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }else{
            Toast.makeText(this, "Não foi possível recuperar o perfil. Verfique a conexão de internet.", Toast.LENGTH_SHORT).show();
        }

    }

    public void ligarAnunciante(View view) {

        String telefone = txtTellefone.getText().toString().replace("(","").replace(")","");
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telefone));
        startActivity(intent);

    }

    public void abrePerfilAnunciante(View view) {

        Intent intent = new Intent(DetalhesAnuncioActivity.this,PerfilAnuncianteActivity.class);
        intent.putExtra("anuncio",this.anuncio);
        startActivity(intent);

    }

}