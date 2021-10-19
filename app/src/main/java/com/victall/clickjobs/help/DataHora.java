package com.victall.clickjobs.help;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataHora {

    public static String recuperaData(){

        //Recupera a data atual
        Date dataAtual = Calendar.getInstance().getTime();

        //Inicializa o SimpleDateFormat passando o formato no construtor
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //utilizaca o objeto dateFormat com o método format para formatar a data atual criada
        String data = dateFormat.format(dataAtual);


        return data;
    }

    public static String recuperaHora(){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        String dataFormatada = sdf.format(hora);

        return dataFormatada;
    }
}
