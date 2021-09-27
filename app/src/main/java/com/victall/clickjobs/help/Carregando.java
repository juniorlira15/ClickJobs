package com.victall.clickjobs.help;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.victall.clickjobs.R;


public class Carregando {

    private static Context context;
    private static AlertDialog alertDialog;
    private static AlertDialog.Builder builder;


    public Carregando(Context context) {
        this.context = context;
    }

    public static void open(Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View title = inflater.inflate(R.layout.custom_title_dialog,null);
        TextView textViewTitle = title.findViewById(R.id.txtTitleDialog);
        textViewTitle.setText("Aguarde...");

        builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(title);
        builder.setView(R.layout.layout_alert_dialog);
        alertDialog = builder.create();
        alertDialog.show();

    }

    public static void close(){
        if(alertDialog!=null) {
            alertDialog.dismiss();
        }
    }
}
