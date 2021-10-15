package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.victall.clickjobs.R;
import com.victall.clickjobs.adapter.ConversasAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.Conversa;

import java.util.ArrayList;

public class ConversasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConversasAdapter adapter;
    private ArrayList<Conversa> conversas_list = new ArrayList<>();
    private Toolbar mToolbar;
    private ChildEventListener childEventListener;
    private DatabaseReference reference;
    private DatabaseReference conversasRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversas);

        inicializaToolbar();

        adapter = new ConversasAdapter(conversas_list,this);

        recyclerView = findViewById(R.id.recuclerConversas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new ConversasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Anuncio anuncio = conversas_list.get(position).getUsuario();
                Intent intent = new Intent(ConversasActivity.this,ChatActivity.class);
                intent.putExtra("anuncio",anuncio);
                startActivity(intent);
            }
        });


    }

    private void inicializaToolbar() {
        mToolbar = findViewById(R.id.toolbarConversas);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Conversas");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaConversas();
    }

    @Override
    protected void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListener);
    }

    private void recuperaConversas(){

        reference = ConfiguracaoFirebase.getDatabaseReference();
        conversasRef = reference.child("conversas").child(UsuarioFirebase.getFirebaseUser().getUid());

        conversas_list.clear();

        childEventListener = conversasRef
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        Conversa conversa = snapshot.getValue(Conversa.class);
                        conversas_list.add(conversa);
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
                });

    }
}