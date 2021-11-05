package com.victall.clickjobs.activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.victall.clickjobs.R;

import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.CheckConnection;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static FirebaseAuth firebaseAuth;
    private EditText edtEmail,edtSenha;
    private TextView txtEsqueciSenha;
    private LoginButton loginButton;
    private Button btnLogin,btnLoginGoogle,btnLoginFace;
    private CallbackManager callbackManager;
    private FirebaseUser firebaseUser;
    private ProgressBar bar;
    private static GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;
    private ProgressBar bar_google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializaViews();

        if(firebaseUser == null){

            FacebookSdk.sdkInitialize(LoginActivity.this);
            loginButton.setPermissions(Arrays.asList("email"));

        }else{
            UsuarioFirebase.verificaUsuarioLogado(this);
        }

        callbackManager = CallbackManager.Factory.create();


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.victall.clickjobs",                  //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", "Excecao PAckgeManger:"+e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", "Excecao NosuchAlgoritm:"+e.getMessage());
        }


        configLoginGoogle();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        handleFacebookToken(loginResult.getAccessToken());
                        bar.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login cancelado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(LoginActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void inicializaViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtSenha = findViewById(R.id.edtSenhaLogin);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenhaLogin);
        btnLogin = findViewById(R.id.btnLogin);
        loginButton = findViewById(R.id.btnfacebookFake);
        bar = findViewById(R.id.progressBarLogin);
        firebaseUser = firebaseAuth.getCurrentUser();
        btnLoginGoogle = findViewById(R.id.sign_in_button);
        bar_google = findViewById(R.id.barGoogle);
        btnLoginFace = findViewById(R.id.btnFace);
    }

    public void cadastrar() {
        startActivity(new Intent(LoginActivity.this,CadastroActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtCadastrar : cadastrar(); break;
            case R.id.btnLogin: login(); break;
            case R.id.btnFace: loginFace(); break;

        }
    }

    public void recuperarSenha(View view){
        startActivity(new Intent(LoginActivity.this,RecuperarSenhaActivity.class));
    }


    private void login() {

        if (CheckConnection.isOnline(this)) {

            String email = edtEmail.getText().toString();
            String senha = edtSenha.getText().toString();


            if ((!email.isEmpty()) && (!senha.isEmpty())) {

                bar.setVisibility(View.VISIBLE);

                ConfiguracaoFirebase.getFirebaseAuth().signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    bar.setVisibility(View.GONE);
                                    startActivity(new Intent(LoginActivity.this, TelaPrincipalActivity.class));
                                    finish();
                                } else {

                                    String excecao;
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        bar.setVisibility(View.GONE);
                                        excecao = "Email não cadastrado";
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        bar.setVisibility(View.GONE);
                                        excecao = "Senha incorreta";
                                    } catch (Exception e) {
                                        bar.setVisibility(View.GONE);
                                        excecao = "Verifique sua conexão de Internet e tente novamente";
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(LoginActivity.this,
                                            excecao,
                                            Toast.LENGTH_LONG).show();

                                }

                            }
                        });

            } else {
                Toast.makeText(this, "Preencha o email e senha corretamente.", Toast.LENGTH_SHORT).show();
            }


        }else{
            Toast.makeText(this, "Parece que você está sem internet.", Toast.LENGTH_SHORT).show();
        }

    }

    private void loginFace() {

        loginButton.performClick();


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void configLoginGoogle(){

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("198997572123-p34d8hkrl0rbjtufd5a5js8q6upjguaf.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.

        //signInButton.setSize(SignInButton.SIZE_STANDARD);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        bar_google.setVisibility(View.VISIBLE);
        btnLoginGoogle.setVisibility(View.INVISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            Toast.makeText(LoginActivity.this, "Seja bem vindo", Toast.LENGTH_LONG).show();
                            fechaProgressBar(bar);
                            UsuarioFirebase.atualizaCadastroGoogle(user.getUid());
                            Intent intent = new Intent(LoginActivity.this, TelaPrincipalActivity.class);
                            intent.putExtra("Key",user.getUid());
                            startActivity(intent);
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            String excecao;
                            try {
                                throw  task.getException();
                            } catch (FirebaseAuthInvalidUserException e){
                                fechaProgressBar(bar);
                                excecao = "User não encontrado.";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                fechaProgressBar(bar);
                                excecao = "Email ou Senha incorreta.";
                            }catch (Exception e){
                                fechaProgressBar(bar);
                                excecao = "Erro ao cadastrar usuário: "+ e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(LoginActivity.this,
                                    excecao,
                                    Toast.LENGTH_LONG).show();

                        }


                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = null;

            try {
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                fechaProgressBar(bar_google);
               Log.d("LOGINGOOGLE", "Problema desconhecido: "+e.getStatusCode());
                e.printStackTrace();

            }

        }
    }

    public void fechaProgressBar(View view){
        bar.setVisibility(View.GONE);
    }

    private void  handleFacebookToken(AccessToken accessToken) {

        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        final DatabaseReference mRef = ConfiguracaoFirebase.getDatabaseReference().child("usuarios");

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Usuario usuario = new Usuario();
                            Log.d("FACEBOOK", "onComplete: Login Sucesso");


                            if (user != null) {

                                String nome = user.getDisplayName();
                                String fotoUrl = String.valueOf(user.getPhotoUrl());

                                usuario.setId(user.getUid());
                                usuario.setNome(nome);
                                usuario.setFoto(Objects.requireNonNull(fotoUrl));
                                usuario.setUltimaAtualizacao(Calendar.getInstance().getTime().toString());


                            mRef.child(user.getUid()).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    UsuarioFirebase.verificaUsuarioLogado(LoginActivity.this);
                                }
                            });

                            }else{
                                Toast.makeText(LoginActivity.this, "Não foi possível obter dados de acesso do Facebook.", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(LoginActivity.this, "Nao foi possível logar no Firebase", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Falha no login com Facebook "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();


        if(currentUser != null){
            currentUser.reload();

            // Não faz nada
            startActivity(new Intent(LoginActivity.this, TelaPrincipalActivity.class));
            finish();

        }
    }

    public static void logout(){

        // Logoff Google
        if(mGoogleSignInClient!=null){
            mGoogleSignInClient.signOut();
        }

        //Logoff Facebook
        if(firebaseAuth!=null) {
            firebaseAuth.signOut();
        }

        LoginManager.getInstance().logOut();

    }
}