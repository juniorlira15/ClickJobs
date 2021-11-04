package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.adapter.AnuncioAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.CheckConnection;
import com.victall.clickjobs.help.Permissoes;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.Endereco;
import com.victall.clickjobs.model.Usuario;
import com.victall.clickjobs.preferences.UsuarioPreferences;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAnuncianteActivity extends AppCompatActivity {


    private TextView nome,sobrenome,telefone,email,nomeCompleto;
    private CircleImageView img1;
    private StorageReference storage;
    private FirebaseUser firebaseUser;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private DatabaseReference databaseReference;
    private Endereco endereco;
    private TextView txtLogradouro,txtEstado,txtPais,txtCep,txtNumero,txtComplemento,txtBairro,txtCidade;
    private AlertDialog.Builder builderProgress;
    private AlertDialog dialogProgress;
    private Usuario usuario;
    private String idAnunciante;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private AnuncioAdapter adapter;
    private ArrayList<Anuncio> anuncios_list;
    private String fotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_anunciante);

        Toolbar toolbar = findViewById(R.id.toolbarPerfilAnunciante);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerAnunciante);
        anuncios_list = new ArrayList<>();
        adapter = new AnuncioAdapter(anuncios_list,this);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            idAnunciante = (String) bundle.getSerializable("id");


        }

        inicializaViews();

        databaseReference = ConfiguracaoFirebase.getDatabaseReference();

        recuperaPerfil();

        recuperaEndereco();

        builderProgress = new AlertDialog.Builder(this);

        View viewLayoutProgress = LayoutInflater.from(this).inflate(R.layout.layout_progress_bar,null);

        builderProgress.setView(viewLayoutProgress);

        dialogProgress = builderProgress.create();

        // recuperando anuncios do anunciante
        getAnunciosAnunciante();

        adapter.setOnItemClickListener(new AnuncioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Anuncio anuncio = anuncios_list.get(position);

                Intent intent = new Intent(getApplicationContext(),DetalheMeuAnuncioActivity.class);
                intent.putExtra("anuncio", anuncio);

                startActivity(intent);
            }
        });
    }

    private void inicializaViews(){
        nome = findViewById(R.id.txtNomePerfilAnunciante);
        sobrenome = findViewById(R.id.txtSobrenomePerfilAnunciante);
        telefone = findViewById(R.id.txtTelefonePerfilAnunciante);
        email = findViewById(R.id.txtEmailPerfilAnunciante);
        nomeCompleto = findViewById(R.id.txtNomeCompletoPerfilAnunciante);
        img1 = findViewById(R.id.imgPerfilAnunciante);
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseUser = UsuarioFirebase.getFirebaseUser();
        // Campos de Endereco
        txtLogradouro = findViewById(R.id.txtPerfilLogradouroAnunciante);
        txtEstado = findViewById(R.id.txtPerfilEstadoAnunciante);
        txtPais = findViewById(R.id.txtPerfilPaisAnunciante);
        txtCep  = findViewById(R.id.txtPerfilCepAnunciante);
        txtNumero = findViewById(R.id.txtPerfilNumeroAnunciante);
        txtComplemento = findViewById(R.id.txtPerfilComplementoAnunciante);
        txtBairro = findViewById(R.id.txtPerfilBairroAnunciante);
        txtCidade = findViewById(R.id.txtPerfilCidadeAnunciante);
    }

    private void recuperaPerfil(){

        if(CheckConnection.isOnline(this)) {

            databaseReference.child("usuarios").child(idAnunciante)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            usuario = snapshot.getValue(Usuario.class);

                            nome.setText(usuario.getNome());
                            sobrenome.setText(usuario.getSobrenome());
                            email.setText(usuario.getEmail());
                            telefone.setText(usuario.getTelefone());
                            nomeCompleto.setText(new StringBuilder().append(usuario.getNome()).append(" ").append(usuario.getSobrenome()).toString());

                            fotoPath = usuario.getFoto();

                            if (!fotoPath.equals("")) {
                                Picasso.get()
                                        .load(fotoPath)
                                        .into(img1);
                            } else {

                                // Se não tiver, busca uma foto salva no Perfil do Firebase
                                Uri imgFirebase = firebaseUser.getPhotoUrl();

                                if (imgFirebase != null) {
                                    Picasso.get()
                                            .load(String.valueOf(imgFirebase))
                                            .into(img1);

                                } else {
                                    // se não encontrar nenhuma foto atualiza com a foto padrão
                                    img1.setImageResource(R.drawable.ic_perfil);
                                }
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

    private void recuperaEndereco(){

        databaseReference.child("enderecos").child(idAnunciante)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        endereco = snapshot.getValue(Endereco.class);

                        if(endereco!=null) {
                            txtLogradouro.setText(endereco.getLogradouro());
                            txtPais.setText(endereco.getPais());
                            txtEstado.setText(endereco.getEstado());
                            txtCep.setText(endereco.getCep());
                            txtNumero.setText(endereco.getNumero());
                            txtComplemento.setText(endereco.getComplemento());
                            txtBairro.setText(endereco.getBairro());
                            txtCidade.setText(endereco.getCidade());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAnunciosAnunciante() {

        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();

        reference.child("meus_anuncios").child(idAnunciante).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Anuncio anuncio = dataSnapshot.getValue(Anuncio.class);

                    anuncios_list.add(anuncio);

                }
                Collections.reverse( anuncios_list );
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void abrirChat(View view){

        Intent intent = new Intent(PerfilAnuncianteActivity.this,ChatActivity.class);
        intent.putExtra("id",idAnunciante);
        intent.putExtra("foto",fotoPath);
        startActivity(intent);

    }
}