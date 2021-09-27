package com.victall.clickjobs.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.victall.clickjobs.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecuperarSenhaActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "recuperarSenha";
    private Button btnEnviar;
    private EditText edtEmail;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        Toolbar toolbar = findViewById(R.id.toolbar_rec_senha);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnEnviar = findViewById(R.id.btnEnviarSenha);
        edtEmail = findViewById(R.id.edtEmailRec);
        lottieAnimationView = findViewById(R.id.animationView);
    }

    private void enviarEmailRecuperarSenha(String email,View view) {

        if (!edtEmail.getText().toString().isEmpty()) {

            if (validaEmail(edtEmail.getText().toString())) {

                //bar.setVisibility(View.VISIBLE);

                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
//                                Toast.makeText(RecuperarSenhaActivity.this, "Instruções de redefinição de senha foram enviadas para seu email ", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(RecuperarSenhaActivity.this, LoginActivity.class));
                                    abreDialog();
                                    //finish();
                                    lottieAnimationView.cancelAnimation();
                                    lottieAnimationView.setVisibility(View.GONE);

                                } else {

                                    Snackbar.make(
                                            view,
                                            "Email não encontrado",
                                            BaseTransientBottomBar.LENGTH_LONG
                                    ).show();
                                    lottieAnimationView.cancelAnimation();
                                    lottieAnimationView.setVisibility(View.GONE);

                                }
                            }
                        });
            }else {
                Snackbar.make(
                        view,
                        "Formato de email Inválido",
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();

            }
        }else{
            Snackbar.make(
                    view,
                    "Informe seu email de cadastro",
                    BaseTransientBottomBar.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEnviarSenha: enviarEmailRecuperarSenha(edtEmail.getText().toString(),view); break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    public void abreDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperação de senha");
        builder.setMessage("Enviamos para você um email com instruções de recuperação de senha.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

        //verifica se é um email válido
        private boolean validaEmail(String email) {

            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");   // sintaxe correta de um endereço email
            Matcher m = p.matcher(email);                    // compara o padrão com o que o usuario digitou
            boolean matchFound = m.matches();                // retorna True ou False de acordo com o resultado encontrado

            return matchFound;
        }

}