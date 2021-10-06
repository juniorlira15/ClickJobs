package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.Login;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.CheckConnection;
import com.victall.clickjobs.help.UsuarioFirebase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;
    private static LatLng currentLocationLatLong;
    private LocationManager lm;
    private static int ALL_PERMISSIONS_RESULT = 101;
    private TextView lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = UsuarioFirebase.getFirebaseUser();
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();

        lat = findViewById(R.id.txtLat);
        lon = findViewById(R.id.txtLong);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Toast.makeText(MainActivity.this, "Latitude: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
                    //Atualiza Coordenadas da Posição do usuario
                    currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                    lat.setText(String.valueOf(location.getLatitude()));
                    lon.setText(String.valueOf(location.getLongitude()));
                }
            }, Looper.myLooper());

        }else{

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Precisamos da sua localização.", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ALL_PERMISSIONS_RESULT);

        }

    }

    public void abreTelaScroll(View view){

    }

    public void abreTelaInicial(View view){
        startActivity(new Intent(MainActivity.this,TelaPrincipalActivity.class));
    }


    public void sair(View view){
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
        //LoginActivity.logout();
    }



    // alerta o usuario se o GPS estiver desativado
    public void alertaGpsDesativado() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS desativado!");
        alertDialog.setMessage("Ativar GPS?");
        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    public void alertaInternetDesativada() {
        final Snackbar mySnackbar = Snackbar.make(findViewById(android.R.id.content), "Sem internet. Conecte-se à rede Wifi ou aos dados móveis.",  Snackbar.LENGTH_INDEFINITE);
        mySnackbar.show();
        mySnackbar.setAction("ENTENDI", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySnackbar.dismiss();
            }
        });
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}