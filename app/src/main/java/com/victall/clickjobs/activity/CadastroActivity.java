package com.victall.clickjobs.activity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.victall.clickjobs.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.model.Usuario;
import com.victall.clickjobs.preferences.UsuarioPreferences;

public class CadastroActivity extends AppCompatActivity implements View.OnClickListener {



    private FirebaseAuth firebaseAuth;
    private static final String TAG = "EmailPassword";
    private EditText edtNome,edtSobrenome,edtEmail,edtTelefone,edtSenha;
    private Button btnCadastrar;
    private static ProgressBar bar;
    private UsuarioPreferences usuarioPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializaViews();
        usuarioPreferences = new UsuarioPreferences(this);

        // Inicializando Instancia do Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Criando Máscara do Campo de Telefone
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");  // cria o formato desejado
        MaskTextWatcher mtw = new MaskTextWatcher(edtTelefone, smf);  // insere o formato no campo a ser preenchido
        edtTelefone.addTextChangedListener(mtw);  // seta o formato no campo texto
    }

    private void inicializaViews() {
//        campoCelular = findViewById(R.id.campoTelefone);
        edtNome = findViewById(R.id.campoNome);
        edtSobrenome = findViewById(R.id.campoSobrenome);
        edtEmail = findViewById(R.id.campoEmail);
        edtTelefone = findViewById(R.id.campoTelefone);
        edtSenha = findViewById(R.id.campoSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        bar = findViewById(R.id.progressBar);

    }

    public static void acionaProgressBar(boolean b){

        if(b) {
            bar.setVisibility(View.VISIBLE);
        }else {
            bar.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null){
            currentUser.reload();
            startActivity(new Intent(CadastroActivity.this, MainActivity.class));
        }
    }


    private boolean verificarCampos() {

        if(!validaSenha(edtSenha.getText().toString())){
            edtSenha.setError("Senha dever conter minimo 6 caracteres.");
            edtSenha.requestFocus();
            return false;
        }else if(!validaCelular(edtTelefone.getText().toString())){
            edtTelefone.setError("Celular inválido");
            edtTelefone.requestFocus();
            return false;
        }else if(!validaEmail(edtEmail.getText().toString())){
            edtEmail.setError("Email inválido");
            edtEmail.requestFocus();
            return false;
        }else if(!validaNome(edtNome.getText().toString())){
            edtNome.setError("Preencha o nome!");
            edtNome.requestFocus();
            return false;
        }else if(!validaSobreNome(edtSobrenome.getText().toString())){
            edtSobrenome.setError("Preencha o sobrenome!");
            edtSobrenome.requestFocus();
            return false;
        }else {
            return true;
        }
    }

    //verifica se o nome foi digitado corretamente
    private boolean validaNome(String nome) {

        if(!nome.isEmpty()){                              // verifica se o campo esta vazio
            return true;
        }else {
            return false;
        }
    }

    //verifica se o nome foi digitado corretamente
    private boolean validaSobreNome(String sobreNome) {

        if(!sobreNome.isEmpty()){                              // verifica se o campo esta vazio
            return true;
        }else {
            return false;
        }
    }




    public void cadastrar(View view) {

        if(verificarCampos()){
            // Aciono a prograbar
            //bar.setVisibility(View.VISIBLE);

            String email = edtEmail.getText().toString();
            String senha = edtSenha.getText().toString();
            Date dataAtual = Calendar.getInstance().getTime();

            final Usuario usuario = new Usuario ();
            usuario.setNome(edtNome.getText().toString());
            usuario.setSobrenome(edtSobrenome.getText().toString());
            usuario.setEmail(edtEmail.getText().toString());
            usuario.setTelefone(edtTelefone.getText().toString());
            usuario.setUltimaAtualizacao(dataAtual.toString());

            // Gravando dados do usuario nas preferencias
            usuarioPreferences.salvarNomeUsuario(edtNome.getText().toString().replace(" ",""));
            usuarioPreferences.salvarSobrenome(edtSobrenome.getText().toString());
            usuarioPreferences.salvarEmailUsuario(edtEmail.getText().toString());
            usuarioPreferences.salvarTelefoneUsuario(edtTelefone.getText().toString());


            firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();

            firebaseAuth.createUserWithEmailAndPassword(email,senha)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                CadastroActivity.acionaProgressBar(false);
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                updateUI(user);

                                String id = task.getResult().getUser().getUid();
                                usuario.setId(id);
                                //usuarioPreferences.salvarIdUsuario(id);
                                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("clientes")
                                        .child(id);

                                mRef.setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(CadastroActivity.this, "Cadastro efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CadastroActivity.this,LoginActivity.class));
                                        finish();

                                    }
                                });



                            } else {
                                acionaProgressBar(false);
                                try {
                                    throw Objects.requireNonNull(task.getException());

                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(CadastroActivity.this, "Email já cadastrado em outra conta. Informe outro email!", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.e("ERRO", e.getMessage());
                                }
                            }
                        }
                    });

        }
    }

    private void updateUI(FirebaseUser user){

        String nomeCompleto = edtNome.getText().toString().concat(" ").concat(edtSobrenome.getText().toString());

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nomeCompleto)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            sendEmailVerification();
                        }
                    }
                });
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
                            //Toast.makeText(CadastroActivity.this, "Email de verificação enviado!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CadastroActivity.this, ConfirmacaoEmailActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(CadastroActivity.this, "Erro ao enviar mensagem de confirmação", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }
                });
        // [END send_email_verification]
    }

    // metodo para verificar se a senha atende aos requisitos minimos
    private boolean validaSenha(String senha) {

        if(senha!=null && senha.length()>=6){
            return true;
        }else{
            return false;
        }

    }

    // verifica se numero foi informado corretamente
    private boolean validaCelular(String celular) {

        if(!celular.isEmpty() && celular.length()>=14){
            return true;
        }else

            return false;
    }

    //verifica se é um email válido
    private boolean validaEmail(String email) {

        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");   // sintaxe correta de um endereço email
        Matcher m = p.matcher(email);                    // compara o padrão com o que o usuario digitou
        boolean matchFound = m.matches();                // retorna True ou False de acordo com o resultado encontrado

        return matchFound;
    }

    public void login(View view) {
        startActivity(new Intent(CadastroActivity.this,LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CadastroActivity.this,LoginActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

    }
}