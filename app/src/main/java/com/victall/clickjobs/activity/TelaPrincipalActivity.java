package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.victall.clickjobs.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.victall.clickjobs.adapter.AnuncioAdapter;
import com.victall.clickjobs.adapter.FiltroAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.AnunciosDAO;

import java.util.ArrayList;
import java.util.Arrays;

public class TelaPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<Anuncio> anuncios_list;
    private RecyclerView recyclerView;
    private AnuncioAdapter adapter;
    private String[] estados;
    private ArrayList<String> estados_list;
    private String[] categorias;
    private ArrayList<String> categorias_list;
    private ExtendedFloatingActionButton actionButton;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);


        inicializaViews();

        // Configuraçoes Iniciais
        estados = getResources().getStringArray(R.array.estados);
        estados_list = new ArrayList<>(Arrays.asList(estados));

        categorias = getResources().getStringArray(R.array.categoria);
        categorias_list = new ArrayList<>(Arrays.asList(categorias));

        //Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new AnuncioAdapter(anuncios_list, this);
        recyclerView.setAdapter( adapter );


        // Config. Drawer
        configDrawer();



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
                //Toast.makeText(TelaPrincipalActivity.this, "Clicou aqui"+position, Toast.LENGTH_SHORT).show();
                detalhesAnuncio(adapter.getAnuncios().get(position));
            }
        });


    }

    private void inicializaViews(){
        anuncios_list = AnunciosDAO.getAnuncios();
        recyclerView = findViewById(R.id.recyclerTelaPrincipal);
        actionButton = findViewById(R.id.fabTelaPrincipal);

    }

    private void configDrawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbarTelaPrincipal);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        if(id == R.id.nav_meus_servicos){startActivity(new Intent(TelaPrincipalActivity.this,MeusServicosActivity.class));}
        if(id == R.id.nav_novo_servico){startActivity(new Intent(TelaPrincipalActivity.this,CadastrarServicoActivity.class));}
        if(id == R.id.nav_meu_perfil){startActivity(new Intent(TelaPrincipalActivity.this,PerfilActivity.class));}
        if(id == R.id.nav_sair){sair();}
        return true;
    }

    public void sair(){
        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
        firebaseAuth.signOut();
        startActivity(new Intent(TelaPrincipalActivity.this,LoginActivity.class));
        finish();
        //LoginActivity.logout();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void filtrarEstado(String estado){

        ArrayList<Anuncio> anuncios_filter = new ArrayList<>();

        //Toast.makeText(this, estado, Toast.LENGTH_SHORT).show();

        for(Anuncio anuncio : anuncios_list){
            if(anuncio.getEndereco().equals(estado)){
                anuncios_filter.add(anuncio);
            }
        }
        if(estado.equals("TODOS")){
            adapter.filterList(anuncios_list);
        }else{
            adapter.filterList(anuncios_filter);
        }

    }

    private void filtrarCategoria(String categoria){

        ArrayList<Anuncio> anuncios_filter = new ArrayList<>();

        for(Anuncio anuncio : anuncios_list){
            if(anuncio.getCategoria().equals(categoria)){
                anuncios_filter.add(anuncio);
            }
        }

        if(categoria.equals("TODAS")){
            adapter.filterList(anuncios_list);
        }else{
            adapter.filterList(anuncios_filter);
        }
    }

    public void abrirDialogCategoria(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Categorias");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View title = inflater.inflate(R.layout.layout_filtros,null);

        RecyclerView recyclerView = title.findViewById(R.id.recylerFiltros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        FiltroAdapter adapter = new FiltroAdapter(categorias_list);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        builder.setView(title);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        adapter.setOnItemClickListener(new FiltroAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                alertDialog.dismiss();
                filtrarCategoria(categorias_list.get(position));
            }
        });


    }

    public void abrirDialogEstado(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Estados");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View title = inflater.inflate(R.layout.layout_filtros,null);

        RecyclerView recyclerView = title.findViewById(R.id.recylerFiltros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        FiltroAdapter adapter = new FiltroAdapter(estados_list);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        builder.setView(title);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        adapter.setOnItemClickListener(new FiltroAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                alertDialog.dismiss();
                filtrarEstado(estados_list.get(position));
            }
        });
    }





}