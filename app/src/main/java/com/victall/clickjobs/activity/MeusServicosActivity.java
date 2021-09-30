package com.victall.clickjobs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.victall.clickjobs.R;
import com.victall.clickjobs.adapter.AnuncioAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;


public class MeusServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private AnuncioAdapter adapter;
    private ArrayList<Anuncio> anuncios_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_servicos);

        Toolbar toolbar = findViewById(R.id.toolbarMeusSericos);
        recyclerView = findViewById(R.id.recyclerMeusServicos);
        recyclerView = new RecyclerView(this);
        anuncios_list = new ArrayList<>();
        adapter = new AnuncioAdapter(anuncios_list,this);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ExtendedFloatingActionButton actionButton = findViewById(R.id.fabMeusServicos);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MeusServicosActivity.this, "asdsadsd", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MeusServicosActivity.this,CadastrarServicoActivity.class));
            }
        });

        getMeusAnuncios();

    }

    private void getMeusAnuncios() {

        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
        FirebaseUser firebaseUser = UsuarioFirebase.getFirebaseUser();

        reference.child("meus_anuncios").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
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


}