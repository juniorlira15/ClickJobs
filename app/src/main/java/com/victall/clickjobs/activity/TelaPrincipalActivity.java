package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.facebook.login.Login;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.victall.clickjobs.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.victall.clickjobs.adapter.AnuncioAdapter;
import com.victall.clickjobs.adapter.FiltroAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.AnunciosDAO;
import com.victall.clickjobs.model.Endereco;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TelaPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,LocationListener{

    //private ArrayList<Anuncio> anuncios_list;
    private RecyclerView recyclerView;
    private AnuncioAdapter adapter;
    private String[] estados;
    private ArrayList<String> estados_list;
    private String[] categorias;
    private ArrayList<String> categorias_list;
    private ExtendedFloatingActionButton actionButton;
    private SlidingUpPanelLayout mUpPanelLayout;
    private ImageView imgFiltro,imgAdd,imgPesquisar;
    private Geocoder geocoder;
    private LocationManager locationManager;
    private LatLng currentLocationLatLong;
    private static int ALL_PERMISSIONS_RESULT = 101;
    private Endereco endereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);


        inicializaViews();

        mUpPanelLayout = findViewById(R.id.sliding_panel);
        mUpPanelLayout.setAnchorPoint(1.0f);
        mUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        // Configuraçoes Iniciais
        estados = getResources().getStringArray(R.array.estados);
        estados_list = new ArrayList<>(Arrays.asList(estados));

        categorias = getResources().getStringArray(R.array.categoria);
        categorias_list = new ArrayList<>(Arrays.asList(categorias));

        //Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new AnuncioAdapter(AnunciosDAO.getAnuncios(), this);
        recyclerView.setAdapter( adapter );

        AnunciosDAO anunciosDAO = new AnunciosDAO();
        anunciosDAO.equals(this);
        //anuncios_list = AnunciosDAO.getAnuncios();
        adapter.notifyDataSetChanged();

        geocoder = new Geocoder(this, Locale.getDefault());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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

        imgFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TelaPrincipalActivity.this,CadastrarServicoActivity.class));
            }
        });

        mUpPanelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        //recuperaEndereco();

    }

    private void inicializaViews(){
        //anuncios_list = AnunciosDAO.getAnuncios();
        recyclerView = findViewById(R.id.recyclerTelaPrincipal);
        actionButton = findViewById(R.id.fabTelaPrincipal);
        imgFiltro = findViewById(R.id.img_filtro);
        imgAdd = findViewById(R.id.img_add);
        imgPesquisar = findViewById(R.id.img_lupa);

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

        //After instantiating your ActionBarDrawerToggle
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.logo_drawer, getApplicationContext().getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
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
        if(id == R.id.nav_meus_servicos){startActivity(new Intent(TelaPrincipalActivity.this,MeusServicosActivity.class));}
        if(id == R.id.nav_novo_servico){startActivity(new Intent(TelaPrincipalActivity.this,CadastrarServicoActivity.class));}
        if(id == R.id.nav_meu_perfil){abrePerfil();}
        if(id == R.id.nav_sair){sair();}
        return true;
    }

    public void sair(){

        LoginActivity.logout();

//        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
//        firebaseAuth.signOut();
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

        for(Anuncio anuncio : AnunciosDAO.getAnuncios()){
            if(anuncio.getEndereco().equals(estado)){
                anuncios_filter.add(anuncio);
            }
        }
        if(estado.equals("TODOS")){
            adapter.filterList(AnunciosDAO.getAnuncios());
        }else{
            adapter.filterList(anuncios_filter);
        }

    }

    private void filtrarCategoria(String categoria){

        ArrayList<Anuncio> anuncios_filter = new ArrayList<>();

        for(Anuncio anuncio : AnunciosDAO.getAnuncios()){
            if(anuncio.getCategoria().equals(categoria)){
                anuncios_filter.add(anuncio);
            }
        }

        if(categoria.equals("TODAS")){
            adapter.filterList(AnunciosDAO.getAnuncios());
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

    private void recuperaEndereco(){

        endereco = new Endereco();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("ENDERECO", "onLocationChanged: Latitude: " + location.getLatitude());
                    //Atualiza Coordenadas da Posição do usuario
                    currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());

                    double latitude = currentLocationLatLong.latitude;
                    double longitude = currentLocationLatLong.longitude;

                    List<Address> addresses  = null;
                    try {
                        addresses = geocoder.getFromLocation(latitude,longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String zip = addresses.get(0).getPostalCode();
                    String country = addresses.get(0).getCountryName();

                    endereco.setCidade(city);
                    endereco.setCep(zip);
                    endereco.setLogradouro(address);
                    endereco.setEstado(state);
                    endereco.setPais(country);
                    endereco.setLatitude(String.valueOf(latitude));
                    endereco.setLongitude(String.valueOf(longitude));


                    DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
                    DatabaseReference enderecoRef = reference.child("enderecos");

                    enderecoRef.child(UsuarioFirebase.getFirebaseUser().getUid())
                            .setValue(endereco);
                }
            }, Looper.myLooper());

        }else{

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Precisamos da sua localização para mostrar Profissionais próximos a voçe.", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ALL_PERMISSIONS_RESULT);

        }

    }

    private void abrePerfil(){

        Intent intent = new Intent(TelaPrincipalActivity.this,PerfilActivity.class);
        //intent.putExtra("endereco",endereco);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}