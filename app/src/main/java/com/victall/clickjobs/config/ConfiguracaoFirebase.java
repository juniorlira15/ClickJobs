package com.victall.clickjobs.config;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.victall.clickjobs.preferences.UsuarioPreferences;


public class ConfiguracaoFirebase {


    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference databaseReference;
    private static FirebaseInstanceId firebaseInstanceId;
    private static FirebaseDatabase firebaseDatabase;
    public static final String NOME_EMPRESA = "Luciana Andrade";
    private static StorageReference referenciaStorage;
    private static UsuarioPreferences usuarioPreferences;

    // Pegando instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAuth(){

        if(firebaseAuth == null)
        {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    // retorna a instancia do Firebase
    public static DatabaseReference getDatabaseReference(){
        if(databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    //Retorna instancia do FirebaseStorage
    public static StorageReference getFirebaseStorage(){
        if( referenciaStorage == null ){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

    public static void setTokenPreferences(Context context){

        usuarioPreferences = new UsuarioPreferences(context);

        firebaseInstanceId.getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if(!task.isSuccessful()){
                            Log.w("Token", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        usuarioPreferences.setTokenUsuario(token);


                    }
                });


    }

}
