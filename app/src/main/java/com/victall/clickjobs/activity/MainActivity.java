package com.victall.clickjobs.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.login.Login;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.UsuarioFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = UsuarioFirebase.getFirebaseUser();
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();


    }

    public void abreTelaScroll(View view){
        startActivity(new Intent(MainActivity.this,ScrollingActivity.class));
    }

    public void abreTelaInicial(View view){
        startActivity(new Intent(MainActivity.this,TelaInicialActivity.class));
    }


    public void sair(View view){
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
        LoginActivity.logout();
    }
}