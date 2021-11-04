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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.adapter.MensagensAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.DataHora;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.Conversa;
import com.victall.clickjobs.model.Mensagem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private Anuncio anuncio;
    private CircleImageView fotoAnunciante;
    private TextView nome;
    private EditText edtMensagem;
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;
    private RecyclerView recyclerView;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagemList;
    private DatabaseReference databaseReference;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagem;
    private ValueEventListener seenListener;
    private String idAnunciante,fotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        inicializaToolbar();

        idUsuarioRemetente = UsuarioFirebase.getFirebaseUser().getUid();

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){

            anuncio = (Anuncio) bundle.getSerializable("anuncio");

            idAnunciante = (String) bundle.getSerializable("id");
            fotoPath = (String) bundle.getSerializable("foto");

            if(anuncio!=null){

                if(anuncio.getFotoAnunciante().equals("")){
                    Picasso.get().load(R.drawable.icon_perfil);
                }else {
                    Picasso.get().load(anuncio.getFotoAnunciante())
                            .into(fotoAnunciante);
                }
                nome.setText(anuncio.getNomeAnunciante());
                idUsuarioDestinatario = anuncio.getIdAnunciante();

            }

            if (idAnunciante != null && fotoPath != null) {

                if(fotoPath.equals("")){
                    Picasso.get().load(R.drawable.icon_perfil);
                }else {
                    Picasso.get().load(fotoPath)
                            .into(fotoAnunciante);
                }

                nome.setText(anuncio.getNomeAnunciante());
                idUsuarioDestinatario = idAnunciante;
            }






        }


        adapter = new MensagensAdapter(mensagemList,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        mensagensRef = databaseReference.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //seenMessage();


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
            mensagem.setHorario(DataHora.recuperaHora());
            mensagem.setSeen(false);

            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario,mensagem);

            salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);

            salvarConversa(mensagem);
        }

    }

    private void salvarConversa(Mensagem mensagem) {

        Conversa conversa = new Conversa();

        conversa.setIdRemetente(idUsuarioRemetente);
        conversa.setIdDestinatario(idUsuarioDestinatario);
        conversa.setUltimaMensagem(mensagem.getMensagem());
        conversa.setUsuario(anuncio);
        conversa.setHorario(mensagem.getHorario());


        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference conversasRef = reference.child("conversas");
        conversasRef.child(idUsuarioRemetente).child(idUsuarioDestinatario)
                .setValue(conversa);

    }

    private void seenMessage(){

        databaseReference = ConfiguracaoFirebase.getDatabaseReference().child("mensagens").child(idUsuarioRemetente).child(idUsuarioDestinatario);

        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                    if(mensagem.getIdUsuario().equals(idUsuarioDestinatario)){

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen",true);
                        snapshot.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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

                        if(mensagem.getIdUsuario().equals(idUsuarioDestinatario)){

                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("seen",true);
                            snapshot.getRef().updateChildren(hashMap);

                        }



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

    private void status(String status){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference().child("usuarios").child(UsuarioFirebase.getFirebaseUser().getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("onffline");
    }
}