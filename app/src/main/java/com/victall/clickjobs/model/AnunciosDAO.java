package com.victall.clickjobs.model;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.victall.clickjobs.activity.TelaPrincipalActivity;
import com.victall.clickjobs.config.ConfiguracaoFirebase;

import java.util.ArrayList;
import java.util.Collections;

public class AnunciosDAO extends AsyncTask {

    private static final ArrayList<Anuncio> ANUNCIOS = new ArrayList<>();

    public static ArrayList<Anuncio> getAnuncios(){
        return ANUNCIOS;
    }

    @Override
    protected ArrayList<Anuncio> doInBackground(Object[] objects) {

        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();

        reference.child("anuncios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ANUNCIOS.clear();

                for(DataSnapshot estados : snapshot.getChildren()) {
                    for (DataSnapshot categorias : estados.getChildren()) {
                        for (DataSnapshot anuncios : categorias.getChildren()) {

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            Log.d("ANUNCIO", "onDataChange: " + anuncio.getTitulo());
                            ANUNCIOS.add(anuncio);
                            //TelaPrincipalActivity.adapter.notifyDataSetChanged();
                        }
                    }
                }


                Collections.reverse(ANUNCIOS);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d("ANUNCIOSDAO", "onPostExecute: "+"Anuncios atualizados");

    }

    public static void addItem(Anuncio anuncio){
        ANUNCIOS.add(anuncio);
    }

    public static void deleteItem(Anuncio anuncio){
        ANUNCIOS.remove(anuncio);
    }


}
