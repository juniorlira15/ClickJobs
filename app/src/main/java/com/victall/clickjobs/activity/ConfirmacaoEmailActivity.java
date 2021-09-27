package com.victall.clickjobs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.victall.clickjobs.R;

public class ConfirmacaoEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView txtMsgConfirmacao,txtVoltarLogin,txtReenviarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_email);

        firebaseAuth = FirebaseAuth.getInstance();
        txtMsgConfirmacao = findViewById(R.id.txtMsgConfirmacao);
        txtVoltarLogin = findViewById(R.id.txtVoltarLogin);
        txtReenviarEmail = findViewById(R.id.txtReenviarEmail);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        String mensagem = getString(R.string.msg_confirmacao_email)+" ".concat("\n\n"+user.getEmail());

        txtMsgConfirmacao.setText(mensagem);

        // verifica se o usuario existe e faz um reload na sessão
        if(user!=null){
            user.reload();

            if(user.isEmailVerified()){
                // Não faz nada
                startActivity(new Intent(ConfirmacaoEmailActivity.this, MainActivity.class));
                finish();
            }else{
                // Força usuario a confirmar email


            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        // verifica se o usuario existe e faz um reload na sessão
        if(user!=null){
            user.reload();

            if(user.isEmailVerified()){
                // Não faz nada
                startActivity(new Intent(ConfirmacaoEmailActivity.this, MainActivity.class));
                finish();
            }else{
                // Força usuario a confirmar email


            }
        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.txtReenviarEmail) {
            sendEmailVerification();
        }

        if(view.getId() == R.id.txtVoltarLogin){
            startActivity(new Intent(ConfirmacaoEmailActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {
                            // Email sent
                            Toast.makeText(ConfirmacaoEmailActivity.this, "Email de verificação enviado!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ConfirmacaoEmailActivity.this, "Ocorreu um erro ao tentar enviar o email. Tenta novamente mais tarde.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END send_email_verification]
    }
}