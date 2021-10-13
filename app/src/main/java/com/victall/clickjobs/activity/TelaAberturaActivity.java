package com.victall.clickjobs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.victall.clickjobs.R;
import com.victall.clickjobs.model.AnunciosDAO;
import com.victall.clickjobs.model.Endereco;


public class TelaAberturaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_abertura);

        AnunciosDAO anunciosDAO = new AnunciosDAO();
        anunciosDAO.execute();

        ImageView logo_bb = findViewById(R.id.logo_inicial_splash);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.splash_transition);

        //logo_bb.startAnimation(animation);

        final Intent intent = new Intent(TelaAberturaActivity.this, LoginActivity.class);


        Thread time = new Thread(){

            public void run(){

                try{
                    sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };

        time.start();

        // ****** Código para ocultar a barra de navegação e de Status.

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // ******* Código para ocultar a barra de navegação e de Status.
    }

}
