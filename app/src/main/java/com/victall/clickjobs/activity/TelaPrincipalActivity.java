package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.victall.clickjobs.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.victall.clickjobs.adapter.AnuncioAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;

public class TelaPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<Anuncio> anuncios_list;
    private RecyclerView recyclerView;
    private AnuncioAdapter adapter;
    private RecyclerView.LayoutManager manager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        recyclerView = findViewById(R.id.recyclerTelaPrincipal);
        anuncios_list = new ArrayList<>();


        //Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new AnuncioAdapter(anuncios_list, this);
        recyclerView.setAdapter( adapter );


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbarTelaPrincipal);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ExtendedFloatingActionButton actionButton = findViewById(R.id.fabTelaPrincipal);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(TelaPrincipalActivity.this, "asdsadsd", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TelaPrincipalActivity.this,CadastrarServicoActivity.class));
            }
        });

        getAnuncios();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.nav_home){startActivity(new Intent(TelaPrincipalActivity.this,MeusServicosActivity.class));}


        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        getAnuncios();
    }

    private void getAnuncios(){

        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();

        reference.child("anuncios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot estados : snapshot.getChildren()) {
                    for (DataSnapshot categorias : estados.getChildren()) {
                        for (DataSnapshot anuncios : categorias.getChildren()) {

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            Log.d("ANUNCIO", "onDataChange: " + anuncio.getTitulo());
                            anuncios_list.add(anuncio);
                        }
                    }

                }
                Collections.reverse( anuncios_list );
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private void filtrarEstado(){

    }





}