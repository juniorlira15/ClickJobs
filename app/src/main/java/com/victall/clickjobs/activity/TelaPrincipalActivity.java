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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.victall.clickjobs.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.victall.clickjobs.adapter.AnuncioAdapter;
import com.victall.clickjobs.help.EndlessRecyclerViewScrollListener;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.AnunciosDAO;

import java.util.ArrayList;

public class TelaPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<Anuncio> anuncios_list;
    private RecyclerView recyclerView;
    private AnuncioAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private EndlessRecyclerViewScrollListener scrollListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        recyclerView = findViewById(R.id.recyclerTelaPrincipal);
        anuncios_list = AnunciosDAO.getAnuncios();


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

        adapter.setOnItemClickListener(new AnuncioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(TelaPrincipalActivity.this, "Clicou aqui"+position, Toast.LENGTH_SHORT).show();
                detalhesAnuncio(adapter.getAnuncios().get(position));
            }
        });


    }

    private void detalhesAnuncio(Anuncio anuncio) {

        anuncio.setCategoria(anuncio.getCategoria());
        anuncio.setIdAnuncio(anuncio.getIdAnuncio());
        anuncio.setDescricao(anuncio.getDescricao());
        anuncio.setTitulo(anuncio.getTitulo());
        anuncio.setValor(anuncio.getValor());
        anuncio.setFoto(anuncio.getFoto());
        anuncio.setData(anuncio.getData());
        anuncio.setEndereco(anuncio.getEndereco());

        Intent intent = new Intent(getApplicationContext(),DetalhesAnuncioActivity.class);

        intent.putExtra("anuncio", anuncio);

        startActivity(intent);


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

    }


    private void filtrarEstado(){

    }





}