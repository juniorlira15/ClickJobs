package com.victall.clickjobs.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.victall.clickjobs.activity.CadastroActivity;
import com.victall.clickjobs.activity.LoginActivity;
import com.victall.clickjobs.activity.TelaPrincipalActivity;
import com.victall.clickjobs.model.Usuario;


import java.io.File;
import java.util.Objects;

import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.preferences.UsuarioPreferences;


public class UsuarioFirebase {


    private static FirebaseAuth firebaseAuth;
    private static UsuarioPreferences usuarioPreferences;

    private static String TAG = "USUARIO_FIREBASE";
    private static StorageReference storageReference;



    public static void  verificaUsuarioLogado(Activity activity){

        try {
            FirebaseUser user = getFirebaseUser();
            if (user != null) {

                Intent intent = new Intent(activity, TelaPrincipalActivity.class);
                activity.startActivity(intent);
                activity.finish();

            }
        }catch (Exception e){
            Log.i("SETUSER","Deu erro ao verificar usuario logado");
        }
    }

    public static void salvar(final Usuario usuario, final Activity activity){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();

        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            Log.d(TAG, "onComplete: Cadastro OK");

                            String idUser = task.getResult().getUser().getUid();

                            usuario.setId(idUser);

                            DatabaseReference mRef = ConfiguracaoFirebase.getDatabaseReference()
                                    .child("usuarios").child(idUser);

                            mRef.setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        CadastroActivity.acionaProgressBar(false);
                                        activity.startActivity(new Intent(activity.getApplicationContext(), TelaPrincipalActivity.class));
                                        Toast.makeText(activity.getApplicationContext(), "Cadastro efetuado com sucesso!", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(activity.getApplicationContext(), "Houve algum erro ao cadastrar!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText( activity,"Erro: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            CadastroActivity.acionaProgressBar(false);
                            try {
                                throw Objects.requireNonNull(task.getException());

                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(activity, "Email já cadastrado em outra conta. Informe outro email!", Toast.LENGTH_LONG).show();
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }

    private static void deletarUsuarioAuth(){
        // Se falhar o cadastro é  apagado o login criado no Firebase
        getFirebaseUser().delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FIREBASE", "onFailure: "+ "Erro ao apagar usuário nao cadastrado da base");
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("FIREBASE", "onFailure: "+ "Sucesso ao apagar usuário nao cadastrado da base");
            }
        });

    }

    public static FirebaseUser getFirebaseUser() {

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();

        return firebaseAuth.getCurrentUser();
    }

    public static void logout() {

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
        firebaseAuth.signOut();

    }

    public static void atualizaNomeUsuario(final String nome){


            FirebaseUser user = getFirebaseUser();

            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Log.d("Perfil", "Exito ao atualizar nome do Perfil");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("PerfilError", "onFailure: "+e.getMessage());
                }
            });




    }

    public static void atualizaCadastroFacebook(final Usuario usuario, final Context context){

        final FirebaseUser user = getFirebaseUser();
        final DatabaseReference mRef = ConfiguracaoFirebase.getDatabaseReference();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(usuario.getFoto()))
                            .setDisplayName(usuario.getNome())
                            .build();
                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d("Perfil", "Sucesso ao atualizar Perfil");
                                context.startActivity(new Intent(context, TelaPrincipalActivity.class));
                                ((LoginActivity )context).finish();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();

                }

            }
        }).start();


    }

    public static void atualizaCadastroFacebook(final String foto, final Context context){

        final FirebaseUser user = getFirebaseUser();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(foto))
                            .build();
                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d("Perfil", "Erro ao atualizar a foto do Perfil");
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();

                }


                DatabaseReference foto_usuario = ConfiguracaoFirebase.getDatabaseReference()
                        .child("usuarios").child(user.getUid()).child("foto");
                DatabaseReference telefone_usuario = ConfiguracaoFirebase.getDatabaseReference().child("usuarios").child(user.getUid()).child("telefone");

                foto_usuario.setValue(String.valueOf(foto)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Foto Atualizada com Sucesso.", Toast.LENGTH_SHORT).show();
                        Carregando.close();
                    }
                });


            }
        }).start();


    }

    public static Usuario getDadosUsuarioLogado(){

        final Usuario[] user = {new Usuario()};
        final FirebaseUser firebaseUser = UsuarioFirebase.getFirebaseUser();
        DatabaseReference mRef = ConfiguracaoFirebase.getDatabaseReference().child("usuarios");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                        if(dataSnapshot.getValue(Usuario.class).getId().equals(firebaseUser.getUid())){
                            user[0] = dataSnapshot.getValue(Usuario.class);
                        }
                    }
                } // implementar algo aqui
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return user[0];

    }

    public static void salvarFotoStorage(final Uri foto, final Context context){

        final FirebaseUser user = getFirebaseUser();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        //coreHelper = new AnstronCoreHelper(context);

        File file = new File(SiliCompressor.with(context).compress(FileUtils.getPath(context,foto),new File(context.getCacheDir(),"temp")));
        final Uri uri = Uri.fromFile(file);

        final StorageReference fotoPerfilRef = storageReference.child("foto_perfil").child(user.getUid());

        //Fazer Upload do Arquivo
        UploadTask uploadTask = fotoPerfilRef.putFile(uri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Toast.makeText(context, "Foto Enviada com Sucesso", Toast.LENGTH_SHORT).show();

                fotoPerfilRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String urlConvertida = uri.toString();
                        UsuarioFirebase.atualizaCadastroFacebook(urlConvertida, context);

                    }
                });
                Log.d(TAG, "onSuccess: "+uri);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Falha ao fazer upload", Toast.LENGTH_SHORT).show();
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });
    }

    public static void atualizaFotoUsuario(final String foto, final Context context){

        final FirebaseUser user = getFirebaseUser();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(foto))
                            .build();
                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //Log.d("Perfil", "Erro ao atualizar a foto do Perfil");
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();

                }
            }
        }).start();


    }

    public static void atualizaCadastroGoogle(String key){

        Usuario usuario = new Usuario();

        FirebaseUser firebaseUser = getFirebaseUser();

        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setFoto(firebaseUser.getPhotoUrl().toString());
        usuario.setId(firebaseUser.getUid());
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.child("usuarios").child(key).setValue(usuario);


    }


}
