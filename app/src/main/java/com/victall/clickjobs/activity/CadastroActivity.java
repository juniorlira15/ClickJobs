package com.victall.clickjobs.activity;

import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.victall.clickjobs.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoCelular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializaViews();

        //Criando Máscara do Campo de Telefone
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");  // cria o formato desejado
        MaskTextWatcher mtw = new MaskTextWatcher(campoCelular, smf);  // insere o formato no campo a ser preenchido
        campoCelular.addTextChangedListener(mtw);  // seta o formato no campo texto
    }

    private void inicializaViews() {
        campoCelular = findViewById(R.id.campoTelefone);
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
}