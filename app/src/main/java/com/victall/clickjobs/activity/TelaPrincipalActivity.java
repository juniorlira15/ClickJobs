package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.victall.clickjobs.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.victall.clickjobs.adapter.AnuncioAdapter;
import com.victall.clickjobs.adapter.FiltroAdapter;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.CheckConnection;
import com.victall.clickjobs.help.OnSwipeTouchListener;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Anuncio;
import com.victall.clickjobs.model.FiltragemCatEst;
import com.victall.clickjobs.model.Endereco;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TelaPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,LocationListener{

    //private ArrayList<Anuncio> anuncios_list;
    private RecyclerView recyclerView;
    public static AnuncioAdapter adapter;
    private String[] estados;
    private ArrayList<FiltragemCatEst> estados_list;
    private String[] categorias;
    private ArrayList<FiltragemCatEst> categorias_list;
    private ExtendedFloatingActionButton actionButton;
    private SlidingUpPanelLayout mUpPanelLayout;
    private ImageView imgFiltro,imgAdd,imgPesquisar;
    private Geocoder geocoder;
    private LocationManager locationManager;
    private LatLng currentLocationLatLong;
    private static int ALL_PERMISSIONS_RESULT = 101;
    private Endereco endereco;
    private EditText edtPesquisar;
    private AlertDialog alertDialogPesquisa;
    private ImageView imgPesquisaEdt,imgApagaPesquisa;
    private static String pesquisaAtual="";
    private static final ArrayList<Anuncio> ANUNCIOS = new ArrayList<>();
    private static final String ONLINE = "online";
    private static final String OFFLINE = "offline";
    private ConstraintLayout constraintLayout;
    private ProgressBar progressBarAtualizar;
    private ImageView imgAtualizar;
    private TextView txtAtualzizar;
    private SeekBar seekBar;
    private TextView txtDistanciaKM,txtFiltroRegiao,txtFiltroCategoria;
    private final int progressPadrao = 10;
    private String filtroEstado = "",filtroCategoria="";


    @SuppressLint("ClickableViewAccessibility")
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
        estados_list = new ArrayList<>();

        categorias = getResources().getStringArray(R.array.categoria);
        categorias_list = new ArrayList<>();
        //Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new AnuncioAdapter(ANUNCIOS, this);
        recyclerView.setAdapter( adapter );

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

        imgPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                abreDialogPesquisa();

            }
        });


        mUpPanelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        //recuperaEndereco();

        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(TelaPrincipalActivity.this) {

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();

                progressBarAtualizar.setVisibility(View.VISIBLE);

                if(CheckConnection.isOnline(TelaPrincipalActivity.this)) {
                    AnunciosDAO anunciosDAO = new AnunciosDAO();
                    anunciosDAO.execute(this);
                }else{
                    Toast.makeText(TelaPrincipalActivity.this, "Verifique sua conexão com a internet e tente novamente.", Toast.LENGTH_SHORT).show();

                    txtAtualzizar.setVisibility(View.GONE);
                    imgAtualizar.setVisibility(View.GONE);
                    Thread time = new Thread(){

                        public void run(){

                            try{
                                sleep(1500);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }

                            finally {


                            }
                        }
                    };

                    time.start();

                    atualizaViews();
                }


            }
        });

        seekBar.setProgress(progressPadrao);
        txtDistanciaKM.setText(progressPadrao+" KM");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                int distancia = Integer.parseInt(management.getEscalaCapitalIni())*i;
//                String escala = round(valor);
                txtDistanciaKM.setText(i+" KM");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(SimulacaoActivity.this, "Clicou", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(SimulacaoActivity.this, "Parou", Toast.LENGTH_SHORT).show();
            }
        });








    }

    private void atualizaViews(){
        progressBarAtualizar.setVisibility(View.GONE);
        txtAtualzizar.setVisibility(View.VISIBLE);
        imgAtualizar.setVisibility(View.VISIBLE);
    }


    private void abreDialogPesquisa() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_pesquisa,null);
        builder.setView(view);

        edtPesquisar = view.findViewById(R.id.edtPesquisar);
        imgPesquisaEdt = view.findViewById(R.id.imgEdtPesquisa);
        imgApagaPesquisa = view.findViewById(R.id.imgApagarPesquisa);

        if(!pesquisaAtual.equals("")){
            edtPesquisar.setText(pesquisaAtual);
            imgApagaPesquisa.setVisibility(View.VISIBLE);
        }else{
            imgApagaPesquisa.setVisibility(View.GONE);
        }

        alertDialogPesquisa = builder.create();


        Window window = alertDialogPesquisa.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FIRST_SUB_WINDOW;
        window.setAttributes(wlp);

        alertDialogPesquisa.show();

        edtPesquisar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().equals("")){
                    imgApagaPesquisa.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals("")) {
                    pesquisaAtual = s.toString();
                    filtrarPesquisa(s.toString());
                    imgApagaPesquisa.setVisibility(View.VISIBLE);
                }
            }
        });

        imgPesquisaEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogPesquisa.dismiss();
            }
        });

        imgApagaPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPesquisar.setText("");
                filtrarPesquisa("");
            }
        });

    }

    private void inicializaViews(){
        //anuncios_list = AnunciosDAO.getAnuncios();
        recyclerView = findViewById(R.id.recyclerTelaPrincipal);
        actionButton = findViewById(R.id.fabTelaPrincipal);
        imgFiltro = findViewById(R.id.img_filtro);
        imgAdd = findViewById(R.id.img_add);
        imgPesquisar = findViewById(R.id.img_lupa);
        constraintLayout = findViewById(R.id.constraintSliderPanel);
        progressBarAtualizar = findViewById(R.id.progressBarAtualizar);
        txtAtualzizar = findViewById(R.id.txtAtualizar);
        imgAtualizar = findViewById(R.id.imgSwipeAtualizar);
        seekBar = findViewById(R.id.seekBarProx);
        txtDistanciaKM = findViewById(R.id.txtFiltroDistancia);
        txtFiltroCategoria = findViewById(R.id.txtFiltroCategoria);
        txtFiltroRegiao = findViewById(R.id.txtFiltroRegiao);
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

        if(CheckConnection.isOnline(this)) {
            Intent intent = new Intent(getApplicationContext(), DetalhesAnuncioActivity.class);
            intent.putExtra("anuncio", anuncio);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Verifique sua conexão com a internet", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.nav_meus_servicos){startActivity(new Intent(TelaPrincipalActivity.this,MeusServicosActivity.class));}
        if(id == R.id.nav_novo_servico){startActivity(new Intent(TelaPrincipalActivity.this,CadastrarServicoActivity.class));}
        if(id == R.id.nav_meu_perfil){abrePerfil();}
        if(id == R.id.nav_sair){sair();}
        if(id == R.id.nav_chat){startActivity(new Intent(TelaPrincipalActivity.this,ConversasActivity.class));}
        return true;
    }

    public void sair(){

        if(CheckConnection.isOnline(this)) {
            status(OFFLINE);
            LoginActivity.logout();
            startActivity(new Intent(TelaPrincipalActivity.this, LoginActivity.class));
            finish();
        }else{
            Toast.makeText(this, "Verifique sua conexão com a internet.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //Toast.makeText(this, "OnRestart", Toast.LENGTH_SHORT).show();
    }

    private void filtrarEstado(String estado){

        filtroEstado = estado;
        ArrayList<Anuncio> anuncios_filter = new ArrayList<>();
        ArrayList<Anuncio> anuncios_filter_categoria = new ArrayList<>();

        //Toast.makeText(this, estado, Toast.LENGTH_SHORT).show();

        for(Anuncio anuncio : ANUNCIOS){
            if(anuncio.getEndereco().equals(estado)){
                anuncios_filter.add(anuncio);
            }
        }

        if(filtroCategoria.equals("") || filtroCategoria.equals("TODAS")) {

            if (estado.equals("TODOS")) {
                adapter.filterList(ANUNCIOS);
            } else {
                adapter.filterList(anuncios_filter);
            }
        }else{

            for(Anuncio anuncio:anuncios_filter){
                if(anuncio.getEndereco().equals(filtroEstado)){
                    anuncios_filter_categoria.add(anuncio);
                }
            }
            adapter.filterList(anuncios_filter_categoria);
        }
    }

    private void filtrarCategoria(String categoria){

        filtroCategoria = categoria;
        ArrayList<Anuncio> anuncios_filter = new ArrayList<>();
        ArrayList<Anuncio> anuncios_filter_estado = new ArrayList<>();

        for(Anuncio anuncio : ANUNCIOS){
            if(anuncio.getCategoria().equals(categoria)){
                anuncios_filter.add(anuncio);
            }
        }

        // Verifica se o Filtro de Estado está ativo
        if(filtroEstado.equals("") || filtroEstado.equals("TODAS")){

            if(categoria.equals("TODAS")){
                adapter.filterList(ANUNCIOS);
            }else{
                adapter.filterList(anuncios_filter);
            }

        }else{

            for(Anuncio anuncio:anuncios_filter){
                if(anuncio.getEndereco().equals(filtroEstado)){
                    anuncios_filter_estado.add(anuncio);
                }
            }
            adapter.filterList(anuncios_filter_estado);
        }


    }

    private void filtrarPesquisa(String pesquisa){

        pesquisa = normaliza(pesquisa);

        ArrayList<Anuncio> anuncios_filter = new ArrayList<>();

        for(Anuncio anuncio : ANUNCIOS){
            if((normaliza(anuncio.getTitulo().toLowerCase())).contains(pesquisa.toLowerCase())
                    || normaliza(anuncio.getCategoria().toLowerCase()).contains(pesquisa.toLowerCase())
                    || normaliza(anuncio.getDescricao().toLowerCase()).contains(pesquisa.toLowerCase())){
                anuncios_filter.add(anuncio);
            }
        }

        adapter.filterList(anuncios_filter);



    }

    private String normaliza(String texto){

        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[^\\p{ASCII}]", "");

        return texto;
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
                filtrarCategoria(categorias_list.get(position).getNome());
                txtFiltroCategoria.setText(categorias_list.get(position).getNome());
                txtFiltroCategoria.setVisibility(View.VISIBLE);
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
                filtrarEstado(estados_list.get(position).getNome());

                txtFiltroRegiao.setText(estados_list.get(position).getNome());
                txtFiltroRegiao.setVisibility(View.VISIBLE);
            }
        });
    }

    public void abrirDialogCategoria2(View view){







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

    private void status(String status){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference().child("usuarios").child(UsuarioFirebase.getFirebaseUser().getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //status(ONLINE);

        if(CheckConnection.isOnline(this)) {
            AnunciosDAO anunciosDAO = new AnunciosDAO();
            anunciosDAO.execute(this);
        }else{
            Toast.makeText(this, "Verifique sua conexão com a internet e tente novamente.", Toast.LENGTH_SHORT).show();
            txtAtualzizar.setVisibility(View.VISIBLE);
            imgAtualizar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            //mUpPanelLayout.setVisibility(View.GONE);
        }
        //Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(UsuarioFirebase.getFirebaseUser()!=null) {
            //status(OFFLINE);
        }
        //Toast.makeText(this, "OnPause", Toast.LENGTH_SHORT).show();
    }

    class AnunciosDAO extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarAtualizar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Anuncio> doInBackground(Object[] objects) {

            DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();

            reference.child("anuncios").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ANUNCIOS.clear();
                    recyclerView.setVisibility(View.VISIBLE);
                    categorias_list.clear();
                    estados_list.clear();

                    int contadorGeral = 0;

                    for (DataSnapshot estados : snapshot.getChildren()) {

                        int contadorEstados = 0;

                        FiltragemCatEst states = new FiltragemCatEst();
                        states.setNome(estados.getRef().getKey());


                        //Log.d("ESTADOS", "estados: "+estados.getRef().getKey());


                        for (DataSnapshot categorias : estados.getChildren()) {

                            //contadorCategoria++;
                            FiltragemCatEst categoria = new FiltragemCatEst();
                            categoria.setNome(categorias.getRef().getKey());
                            categoria.setContador((int) categorias.getChildrenCount());
                            categorias_list.add(categoria);

                            Log.d("ESTADOS", "categorias: "+categorias.getRef().getKey());

                            for (DataSnapshot anuncios : categorias.getChildren()) {

                                contadorGeral++;
                                contadorEstados++;

                                Anuncio anuncio = anuncios.getValue(Anuncio.class);
                                Log.d("CONTADOR", "categorias: " + contadorGeral);
                                ANUNCIOS.add(anuncio);
                                TelaPrincipalActivity.adapter.notifyDataSetChanged();
                            }


                        }

                        states.setContador(contadorEstados);
                        estados_list.add(states);
                    }



                    Collections.reverse(ANUNCIOS);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBarAtualizar.setVisibility(View.GONE);
                    Toast.makeText(TelaPrincipalActivity.this, "Erro desconhecido", Toast.LENGTH_SHORT).show();
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("ANUNCIOSDAO", "onPostExecute: " + "Anuncios atualizados");
            progressBarAtualizar.setVisibility(View.GONE);
            txtAtualzizar.setVisibility(View.GONE);
            imgAtualizar.setVisibility(View.GONE);

        }
    }

    public static void addItem(Anuncio anuncio){
        ANUNCIOS.add(anuncio);
    }

    public static void deleteItem(Anuncio anuncio){
        ANUNCIOS.remove(anuncio);
    }

    public void fechaPanel(View view){

       mUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

    }

}