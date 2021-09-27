package com.victall.clickjobs.activity.model;

import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Anuncio {

    private ImageView imageView;
    private TextView textViewNome;
    private TextView txtCategoria;
    private TextView txtFaixaValor;
    private TextView txtEndereco;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTextViewNome() {
        return textViewNome;
    }

    public void setTextViewNome(TextView textViewNome) {
        this.textViewNome = textViewNome;
    }

    public TextView getTxtCategoria() {
        return txtCategoria;
    }

    public void setTxtCategoria(TextView txtCategoria) {
        this.txtCategoria = txtCategoria;
    }

    public TextView getTxtFaixaValor() {
        return txtFaixaValor;
    }

    public void setTxtFaixaValor(TextView txtFaixaValor) {
        this.txtFaixaValor = txtFaixaValor;
    }

    public TextView getTxtEndereco() {
        return txtEndereco;
    }

    public void setTxtEndereco(TextView txtEndereco) {
        this.txtEndereco = txtEndereco;
    }
}
