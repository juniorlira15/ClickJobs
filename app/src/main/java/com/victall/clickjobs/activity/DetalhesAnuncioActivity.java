package com.victall.clickjobs.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.victall.clickjobs.R;
import com.victall.clickjobs.model.Anuncio;

import java.util.ArrayList;
import java.util.List;

public class DetalhesAnuncioActivity extends AppCompatActivity {

    private Anuncio anuncio;
    private TextView txtDesc,txtCategoria,txtValor,txtEndereco,txtTitulo,txtNome;
    private Toolbar mToolbar;
    private ImageSlider slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_anuncio);

        Bundle bundle = getIntent().getExtras();


        inicializaToolbar();

        //txtDesc = findViewById(R.id.txtDescricao);
        txtCategoria = findViewById(R.id.txtCategoria);
        //txtEndereco = findViewById(R.id.txtEndereco);
        txtValor = findViewById(R.id.txtValor);
        txtTitulo = findViewById(R.id.txtChatNomeAnunciante);
        slider = findViewById(R.id.imageSliderDetalhesAnuncio);
        txtNome = findViewById(R.id.txtNomeAnunciante);


        if (bundle != null) {

            anuncio = (Anuncio) bundle.getSerializable("anuncio");
            this.anuncio = anuncio;

            txtValor.setText(anuncio.getValor());
//            txtEndereco.setText(anuncio.getEndereco());
            txtCategoria.setText(anuncio.getCategoria());
    //        txtDesc.setText(anuncio.getDescricao());
            txtTitulo.setText(anuncio.getTitulo());
            txtNome.setText(anuncio.getNomeAnunciante());

            List<SlideModel> slideModels = new ArrayList<>();

            for(int i=0; i<anuncio.getFoto().size();i++){
                slideModels.add(new SlideModel(anuncio.getFoto().get(i), ""));
            }
            slider.setImageList(slideModels,true);




        }else{
            Toast.makeText(this, "Erro ao consultar informações.", Toast.LENGTH_SHORT).show();
        }


    }

    public void inicializaToolbar(){
        mToolbar = findViewById(R.id.toolbarDetalhesAnuncio);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    public void abrirChat(View view){

        Intent intent = new Intent(DetalhesAnuncioActivity.this,ChatActivity.class);
        intent.putExtra("anuncio",anuncio);
        startActivity(intent);



    }
}