package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.Permissoes;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.preferences.UsuarioPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private UsuarioPreferences preferences;
    private TextView nome,sobrenome,telefone,email,nomeCompleto;
    private String caminhoImagem;
    private CircleImageView img1;
    private StorageReference storage;
    private FirebaseUser firebaseUser;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarMeuPerfil);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        inicializaViews();


        preferences = new UsuarioPreferences(this);

        nome.setText(preferences.getNomeUsuario());
        sobrenome.setText(preferences.getSobrenomeUsuario());
        email.setText(preferences.getEmailUsuario());
        telefone.setText(preferences.getTelefoneUsuario());
        nomeCompleto.setText(new StringBuilder().append(preferences.getNomeUsuarioCompleto()).append(" ").append(preferences.getSobrenomeUsuario()).toString());

        String fotoPath=preferences.getFotoUsuario();

        if(!fotoPath.equals("")) {
            loadImageFromStorage(fotoPath);
        }else{
            // Se não tiver, busca uma foto salva no Perfil do Firebase
            Uri imgFirebase = UsuarioFirebase.getFirebaseUser().getPhotoUrl();
            if(imgFirebase!= null){
                Picasso.get().load(String.valueOf(imgFirebase)).into(img1);
                try {
                    // Depois de achar a foto atualiza as preferencias e salva no armazenamento interno
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgFirebase);
                    preferences.salvarFotoUsuario(saveToInternalStorage(bitmap));
                } catch (IOException e) {
                    img1.setImageResource(R.drawable.ic_perfil);
                    e.printStackTrace();
                }
            }else{
                // se não encontrar nenhuma foto atualiza com a foto padrão
                img1.setImageResource(R.drawable.ic_perfil);
            }
        }


        Permissoes.validarPermissoes(permissoes,this,1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão Negadas");
        builder.setMessage("Para utilizar o App é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void inicializaViews(){
        nome = findViewById(R.id.txtNomePerfil);
        sobrenome = findViewById(R.id.txtSobrenomePerfil);
        telefone = findViewById(R.id.txtTelefonePerfil);
        email = findViewById(R.id.txtEmailPerfil);
        nomeCompleto = findViewById(R.id.txtNomeCompletoPerfil);
        img1 = findViewById(R.id.imgMeuPerfil);
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseUser = UsuarioFirebase.getFirebaseUser();
    }


    public void alteraFoto(View view){


        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == Activity.RESULT_OK){

            // RECUPERAR IMAGEM
            Uri imagemSelecionada = data.getData();
            String caminhaImagem = imagemSelecionada.toString();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);
                preferences.salvarFotoUsuario(saveToInternalStorage(bitmap));

            } catch (IOException e) {
                e.printStackTrace();
            }


            if (requestCode == 1) {
                Picasso.get().load(imagemSelecionada)
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder_error)
                        .into(img1);
            }

            this.caminhoImagem  = caminhaImagem;

            salvarFotoStorage(caminhaImagem);



        }


    }

    private void salvarFotoStorage(String urlString){

        //Criar nÃ³ no storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("usuarios")
                .child( firebaseUser.getUid() )
                .child("perfil");

        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.child("clientes").child(firebaseUser.getUid()).child("foto");

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile( Uri.parse(urlString) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlConvertida = uri.toString();

                        UsuarioFirebase.atualizaFotoUsuario(urlConvertida, PerfilActivity.this);

                        databaseReference.setValue(urlConvertida).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(PerfilActivity.this, "Foto salva com sucesso.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //exibirMensagemErro("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
                Toast.makeText(PerfilActivity.this, "Algum erro ocorreu. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path)
    {
        try {
            File f=new File(path, "/profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            img1.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}