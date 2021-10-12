package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.adapter.MensagensAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.Mensagem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private Anuncio anuncio;
    private CircleImageView fotoAnunciante;
    private TextView nome;
    private EditText edtMensagem;
    private String idUsuarioRemetente;
    private String udUsuarioDestinatario;
    private RecyclerView recyclerView;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagemList;
    private DatabaseReference databaseReference;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        inicializaToolbar();

        idUsuarioRemetente = UsuarioFirebase.getFirebaseUser().getUid();

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){

            anuncio = (Anuncio) bundle.getSerializable("anuncio");

            if(anuncio.getFotoAnunciante().equals("")){
                Picasso.get().load(R.drawable.icon_perfil);
            }else {
                Picasso.get().load(anuncio.getFotoAnunciante())
                        .into(fotoAnunciante);
            }
            nome.setText(anuncio.getNomeAnunciante());
            udUsuarioDestinatario = anuncio.getIdAnunciante();


        }


        adapter = new MensagensAdapter(mensagemList,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        mensagensRef = databaseReference.child("mensagens")
                .child(idUsuarioRemetente)
                .child(udUsuarioDestinatario);



    }

    public void inicializaToolbar(){
        edtMensagem = findViewById(R.id.edtMensagemChat);
        mToolbar = findViewById(R.id.toolbarChat);
        nome = findViewById(R.id.txtChatNomeAnunciante);
        fotoAnunciante = findViewById(R.id.imgFotoAnunciante);
        recyclerView = findViewById(R.id.recyclerMensagens);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        mensagemList = new ArrayList<>();


    }

    public void enviarMensagem(View view) {

        String textoMsg = edtMensagem.getText().toString();

        if(!textoMsg.isEmpty()){

            Mensagem mensagem = new Mensagem();
            mensagem.setMensagem(textoMsg);
            mensagem.setIdUsuario(idUsuarioRemetente);

            salvarMensagem(idUsuarioRemetente,udUsuarioDestinatario,mensagem);

            salvarMensagem(udUsuarioDestinatario,idUsuarioRemetente,mensagem);
        }

    }


    private void salvarMensagem(String idRemetente, String idDestinatario,Mensagem mensagem){

        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference mensagemRef = reference.child("mensagens");

        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(mensagem)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        edtMensagem.setText("");

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagem);
    }

    private void recuperarMensagens(){

        childEventListenerMensagem = mensagensRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Mensagem mensagem = snapshot.getValue(Mensagem.class);
                        mensagemList.add(mensagem);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

    }
}